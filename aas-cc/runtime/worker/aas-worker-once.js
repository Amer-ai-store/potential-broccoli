"use strict";
const fs=require("node:fs");
const path=require("node:path");
const crypto=require("node:crypto");
const {spawn}=require("node:child_process");
const BASE="C:\\AAS\\local-ai-poc";
const API="https://zvedrouuboxgbanzdnio.supabase.co/functions/v1/aas-signed-local-worker-bridge";
const WORKER="JAWAFFASVR-CPU-WORKER-V1";
const KEY=fs.readFileSync(path.join(BASE,"worker-private.pem"),"utf8");
const MODEL=path.join(BASE,"qwen3-0.6b-q4_k_m.gguf");
const CLI=path.join(BASE,"bin","llama-cli.exe");
const LOG=path.join(BASE,"worker-last-run.json");
const MAX_OUT=80000;
let CLOCK_OFFSET_MS=0;
async function synchronizeClock(){
  const started=Date.now();
  const response=await fetch(API+'/health',{signal:AbortSignal.timeout(12000)});
  const received=Date.now();
  if(!response.ok)throw new Error('CLOCK_SYNC_HEALTH_FAILED');
  const health=await response.json();
  if(!health.ok||!Number.isFinite(health.server_time_ms))throw new Error('CLOCK_SYNC_RESPONSE_INVALID');
  CLOCK_OFFSET_MS=Math.round(health.server_time_ms-(started+received)/2);
  if(Math.abs(CLOCK_OFFSET_MS)>24*3600000)throw new Error('CLOCK_OFFSET_EXCESSIVE');
  event('CLOCK_SYNC',{server_clock_offset_seconds:Math.round(CLOCK_OFFSET_MS/1000)});
}
function event(type,extra={}) { console.log(JSON.stringify({at:new Date().toISOString(),type,...extra})); }
async function invoke(action,data={}){
  const body=JSON.stringify({action,...data});
  const ts=String(Date.now()+CLOCK_OFFSET_MS);
  const nonce=crypto.randomUUID();
  const signature=crypto.sign("RSA-SHA256",Buffer.from(ts+"."+nonce+"."+body),KEY).toString("base64");
  const result=await fetch(API,{method:"POST",headers:{"content-type":"application/json","x-aas-worker":WORKER,"x-aas-timestamp":ts,"x-aas-nonce":nonce,"x-aas-signature":signature},body,signal:AbortSignal.timeout(20000)});
  const response=await result.json().catch(()=>({ok:false,error:"RESPONSE_NOT_JSON"}));
  if(!result.ok||!response.ok)throw new Error("BRIDGE_"+result.status+"_"+String(response.error||"UNKNOWN"));
  return response;
}
function extractAnswer(stdout){
  const normalized=stdout.replace(/\r/g,"");
  const match=normalized.match(/\n> [^\n]*\n\s*([\s\S]*?)\n\[ Prompt:/);
  if(!match)return "";
  return match[1].replace(/<think>[\s\S]*?<\/think>/g,"").trim().slice(0,3500);
}
async function infer(prompt){
  return new Promise((resolve)=>{
    const start=Date.now();
    const args=["-m",MODEL,"-p",prompt,"-n","48","-t","1","-c","768","--single-turn","--reasoning","off","--reasoning-budget","0","--no-display-prompt","--simple-io"];
    const proc=spawn(CLI,args,{windowsHide:true,stdio:["ignore","pipe","pipe"],env:{...process.env,LLAMA_ARG_THREADS:"1"}});
    let stdout="",stderr="",complete=false;
    let timer;
    function finish(error=null){
      if(complete)return;
      complete=true;clearTimeout(timer);
      resolve({output:extractAnswer(stdout),execution_ms:Date.now()-start,run_error:error||null,stderr_excerpt:stderr.slice(-220)});
    }
    timer=setTimeout(()=>{proc.kill();finish("LOCAL_MODEL_TIMEOUT")},75000);
    proc.stdout.on("data",x=>{stdout+=x.toString("utf8");if(stdout.length>MAX_OUT){proc.kill();finish("OUTPUT_TOO_LARGE")};});
    proc.stderr.on("data",x=>{stderr+=x.toString("utf8");if(stderr.length>MAX_OUT)stderr=stderr.slice(-MAX_OUT);});
    proc.on("error",()=>finish("MODEL_PROCESS_START_FAILED"));
    proc.on("close",code=>finish(code===0?null:"MODEL_EXIT_"+code));
  });
}
async function main(){
 await synchronizeClock();
 if(!fs.existsSync(MODEL)||!fs.existsSync(CLI))throw new Error("LOCAL_MODEL_FILES_MISSING");
 const beat=await invoke("heartbeat");
 event("SIGNED_HEARTBEAT",{queued:beat.queued});
 const poll=await invoke("poll");
 if(!poll.job){event("IDLE_NO_JOB");return;}
 const job=poll.job;event("CLAIMED",{job_id:job.id,kind:job.kind});
 const answer=await infer(job.prompt);
 const res=await invoke("submit",{job_id:job.id,lease_token:job.lease_token,model:"Qwen3-0.6B-Q4_K_M/llama.cpp-b11910",output:answer.output,execution_ms:answer.execution_ms,run_error:answer.run_error||(!answer.output?"EMPTY_MODEL_OUTPUT":"")});
 const summary={at:new Date().toISOString(),job_id:job.id,execution_ms:answer.execution_ms,output:answer.output,state:res.state,qa:res.qa,canonical_done:res.canonical_done};
 fs.writeFileSync(LOG,JSON.stringify(summary,null,2),"utf8");
 event("DONE_SUBMITTED",{job_id:job.id,qa:res.qa,state:res.state,execution_ms:answer.execution_ms,canonical_done:false});
}
main().catch(e=>{const msg=String(e.message||e).slice(0,160);event("WORKER_FAILED",{error:msg});try{fs.writeFileSync(LOG,JSON.stringify({at:new Date().toISOString(),error:msg,worker:"AAS_LOCAL_SERVICE"},null,2),"utf8")}catch{};process.exitCode=1;});
