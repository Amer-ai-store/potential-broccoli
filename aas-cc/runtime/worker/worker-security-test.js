"use strict";
const fs=require("fs"),crypto=require("crypto");
const endpoint="https://zvedrouuboxgbanzdnio.supabase.co/functions/v1/aas-signed-local-worker-bridge";
const key=fs.readFileSync("C:\\AAS\\local-ai-poc\\worker-private.pem","utf8");
async function send(headers,body){const r=await fetch(endpoint,{method:"POST",headers:{"content-type":"application/json",...headers},body});return {status:r.status,body:await r.json().catch(()=>({}))};}
async function main(){
  const unauth=await send({},JSON.stringify({action:"heartbeat"}));
  if(unauth.status!==401)throw Error("UNAUTHENTICATED_NOT_REJECTED:"+unauth.status);
  console.log("UNSIGNED_REJECTED=401");
  const started=Date.now();const r=await fetch(endpoint+"/health");const clock=await r.json();const finished=Date.now();
  const timestamp=String(Math.round((started+finished)/2)+Math.round(clock.server_time_ms-(started+finished)/2));
  const nonce=crypto.randomUUID(),body=JSON.stringify({action:"heartbeat"});
  const sig=crypto.sign("RSA-SHA256",Buffer.from(timestamp+"."+nonce+"."+body),key).toString("base64");
  const headers={"x-aas-worker":"JAWAFFASVR-CPU-WORKER-V1","x-aas-timestamp":timestamp,"x-aas-nonce":nonce,"x-aas-signature":sig};
  const first=await send(headers,body);if(first.status!==200)throw Error("FIRST_SIGNED_REQUEST_FAILED:"+first.status+":"+first.body.error);
  console.log("SIGNED_HEARTBEAT_ACCEPTED=200");
  const replay=await send(headers,body);if(replay.status!==409||replay.body.error!=="REPLAY_BLOCKED")throw Error("REPLAY_NOT_BLOCKED:"+replay.status);
  console.log("REPLAY_BLOCKED=409");
  const tampered=await send({...headers,"x-aas-nonce":crypto.randomUUID()},body);if(tampered.status!==401)throw Error("TAMPER_NOT_REJECTED:"+tampered.status);
  console.log("SIGNATURE_TAMPER_REJECTED=401");
  console.log("SECURITY_NEGATIVE_TESTS=4/4");
}
main().catch(e=>{console.log("SECURITY_TEST_ERROR="+String(e.message||e).slice(0,180));process.exitCode=1});
