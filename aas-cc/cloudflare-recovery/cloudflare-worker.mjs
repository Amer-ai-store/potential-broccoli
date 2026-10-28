/* AMER AI Store - Cloudflare Workers AI recovery CANDIDATE v1.2.1
 * NOT deployed. Staging only; inference is proven solely by AI.run result.
 * Bindings: AI, AAS_RATE_LIMITER (Rate Limiting), AAS_BRIDGE_SECRET, optional AAS_BRIDGE_SECRET_NEXT during rotation.
 */
const VERSION = 'aas-cloudflare-free-canary-1.2.1-CANDIDATE';
const MAX_BODY_BYTES = 12000;
const RATE_LIMIT_KEY = 'aas-bridge';
const MODEL = '@cf/google/gemma-4-26b-a4b-it';
const FIXTURES = Object.freeze({
 POS:'All mandatory evidence, approvals, security review, and independent QA are verified. Return REVIEWED_OK.',
 CHANGE:'A required artifact revision is outstanding and review is pending. Return CHANGE_REQUEST.',
 BLOCK:'A mandatory policy dependency is unavailable and execution is prohibited. Return BLOCKED.'
});
const EXPECTED = Object.freeze({POS:'REVIEWED_OK',CHANGE:'CHANGE_REQUEST',BLOCK:'BLOCKED'});
function reply(body,status=200){return new Response(JSON.stringify(body),{status,headers:{'content-type':'application/json; charset=utf-8','cache-control':'no-store','x-content-type-options':'nosniff'}})}
const encoded=s=>new TextEncoder().encode(s);
async function constantTimeTokenMatch(input,secret){
 if(!input||typeof secret!=='string'||secret.length<24||input.length>1024)return false;
 const hashes=await Promise.all([input,secret].map(async v=>new Uint8Array(await crypto.subtle.digest('SHA-256',encoded(v)))));
 let v=0;for(let i=0;i<hashes[0].length;i++)v|=hashes[0][i]^hashes[1][i];
 return v===0;
}
// Accept the current and the next secret so rotation never needs a simultaneous Worker + Supabase update.
async function bridgeTokenMatches(input,env){
 const matches=await Promise.all([env?.AAS_BRIDGE_SECRET,env?.AAS_BRIDGE_SECRET_NEXT].map(s=>constantTimeTokenMatch(input,s)));
 return matches.some(Boolean);
}
// Reads at most max bytes; returns null once the stream exceeds it, whatever Content-Length claimed.
async function readCapped(stream,max){
 if(!stream)return new Uint8Array(0);
 const reader=stream.getReader();const chunks=[];let size=0;
 for(;;){
  const {done,value}=await reader.read();
  if(done)break;
  size+=value.byteLength;
  if(size>max){await reader.cancel().catch(()=>{});return null}
  chunks.push(value);
 }
 const out=new Uint8Array(size);let offset=0;
 for(const c of chunks){out.set(c,offset);offset+=c.byteLength}
 return out;
}
function getModelText(output){
 const v=output?.response??output?.choices?.[0]?.message?.content??output?.result?.response;
 return typeof v==='string'?v.trim():'';
}
function strictDecision(text){
 let value;
 try{value=JSON.parse(text)}catch{return null}
 if(!value||typeof value!=='object'||Array.isArray(value))return null;
 if(typeof value.checkpoint_status!=='string'||typeof value.summary!=='string'||!value.summary.trim())return null;
 if(!['REVIEWED_OK','CHANGE_REQUEST','BLOCKED'].includes(value.checkpoint_status))return null;
 return value;
}
export default {
 async fetch(request,env){
  const url=new URL(request.url);
  if(request.method==='GET'&&url.pathname==='/health'){
   // Unauthenticated: report liveness only, not model or binding/secret configuration.
   return reply({ok:true,service_online:true,version:VERSION,phase:'STAGING_ONLY',inference_verified_live:false,done_verified:false});
  }
  if(!['/v1/canary','/v1/infer'].includes(url.pathname))return reply({ok:false,error:'NOT_FOUND'},404);
  if(request.method!=='POST')return reply({ok:false,error:'METHOD_NOT_ALLOWED'},405);
  const authorization=request.headers.get('authorization')||'';
  if(!authorization.startsWith('Bearer ')||!(await bridgeTokenMatches(authorization.slice(7),env)))return reply({ok:false,error:'UNAUTHORIZED'},401);
  // Fail closed without a limiter: a leaked token or a dispatcher retry loop must not drain the free-tier budget.
  if(!env?.AAS_RATE_LIMITER?.limit)return reply({ok:false,error:'RATE_LIMITER_UNAVAILABLE'},503);
  try{
   const {success}=await env.AAS_RATE_LIMITER.limit({key:RATE_LIMIT_KEY});
   if(!success)return reply({ok:false,error:'RATE_LIMITED'},429);
  }catch{return reply({ok:false,error:'RATE_LIMITER_FAILED'},503)}
  if(!env?.AI?.run)return reply({ok:false,error:'AI_BINDING_UNAVAILABLE'},503);
  const declared=request.headers.get('content-length');
  if(declared!==null&&!/^\d+$/.test(declared))return reply({ok:false,error:'INVALID_CONTENT_LENGTH'},400);
  if(declared!==null&&Number(declared)>MAX_BODY_BYTES)return reply({ok:false,error:'PAYLOAD_TOO_LARGE'},413);
  const raw=await readCapped(request.body,MAX_BODY_BYTES);
  if(!raw)return reply({ok:false,error:'PAYLOAD_TOO_LARGE'},413);
  let body;try{body=JSON.parse(new TextDecoder().decode(raw))}catch{return reply({ok:false,error:'INVALID_JSON'},400)}
  const isCanary=url.pathname==='/v1/canary';
  const check=String(body?.test||'').toUpperCase();
  if(isCanary&&!(check in FIXTURES))return reply({ok:false,error:'INVALID_CANARY_TEST'},400);
  const correlation=String(body?.correlation_id||'').slice(0,120);
  const system=isCanary?'Return one strict JSON object with checkpoint_status and summary; no Markdown.':String(body?.system||'').slice(0,3000);
  const prompt=isCanary?FIXTURES[check]:String(body?.prompt||'').slice(0,8000);
  if(!prompt.trim())return reply({ok:false,error:'EMPTY_PROMPT'},400);
  try{
   const output=await env.AI.run(MODEL,{messages:[{role:'system',content:system},{role:'user',content:prompt}],max_completion_tokens:isCanary?192:768,temperature:0});
   const text=getModelText(output);
   if(!text)return reply({ok:false,error:'EMPTY_MODEL_OUTPUT'},502);
   if(isCanary){
    const d=strictDecision(text);
    if(!d||d.checkpoint_status!==EXPECTED[check])return reply({ok:false,error:'CANARY_DECISION_MISMATCH',test:check,inference_executed:true},422);
    return reply({ok:true,test:check,checkpoint_status:d.checkpoint_status,text:JSON.stringify(d),model:MODEL,provider:'cloudflare-workers-ai',provider_family:'cloudflare',inference_executed:true,correlation_id:correlation});
   }
   return reply({ok:true,text,model:MODEL,provider:'cloudflare-workers-ai',provider_family:'cloudflare',inference_executed:true,correlation_id:correlation});
  }catch{return reply({ok:false,error:'MODEL_INFERENCE_FAILED'},502)}
 }
};
