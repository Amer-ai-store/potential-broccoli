/* Amer AI Store - AAS-CC Cloudflare dispatcher adapter, REVIEW CANDIDATE ONLY.
 * Never store credential values in source, logs, or database evidence.
 * Supabase Vault secret presence does not automatically populate Deno.env.
 */
export const CLOUDFLARE_PROVIDER='cloudflare-workers-ai';
export const CLOUDFLARE_FAMILY='cloudflare';
export const APPROVED_WORKER_ORIGIN='https://aas-free-generative-canary.zxzx-mohammad2027.workers.dev';
function config(env){
 const secret=env.CLOUDFLARE_AAS_BRIDGE_SECRET;
 const origin=env.AAS_CLOUDFLARE_WORKER_URL||APPROVED_WORKER_ORIGIN;
 if(typeof secret!=='string'||secret.length<24)throw new Error('CLOUDFLARE_EDGE_SECRET_REF_NOT_READY');
 let u;try{u=new URL(origin)}catch{throw new Error('INVALID_CLOUDFLARE_ORIGIN')}
 if(u.origin!==APPROVED_WORKER_ORIGIN||u.pathname!=='/'||u.search||u.hash)throw new Error('UNAPPROVED_CLOUDFLARE_ORIGIN');
 return {secret,origin:u.origin};
}
const MAX_RESPONSE_BYTES=18000;
function safeErr(code,status){const e=new Error(code);e.status=status;return e}
// Stops reading once the body exceeds max bytes instead of buffering the whole response first.
async function readCappedText(res,max){
 if(!res.body)return '';
 const reader=res.body.getReader();const chunks=[];let size=0;
 for(;;){
  const {done,value}=await reader.read();
  if(done)break;
  size+=value.byteLength;
  if(size>max){await reader.cancel().catch(()=>{});return null}
  chunks.push(value);
 }
 const out=new Uint8Array(size);let offset=0;
 for(const c of chunks){out.set(c,offset);offset+=c.byteLength}
 return new TextDecoder().decode(out);
}
export async function cloudflareGenerate({system,prompt,correlationId='',env,fetchFn=fetch,timeoutMs=15000}){
 const {secret,origin}=config(env);
 const controller=new AbortController();
 const timer=setTimeout(()=>controller.abort(),timeoutMs);
 try{
  const res=await fetchFn(origin+'/v1/infer',{
   method:'POST',redirect:'error',
   headers:{'content-type':'application/json','authorization':'Bearer '+secret},
   body:JSON.stringify({system,prompt,correlation_id:correlationId}),
   signal:controller.signal});
  if(!res.ok)throw safeErr('CLOUDFLARE_INFER_HTTP_'+res.status,res.status);
  const responseText=await readCappedText(res,MAX_RESPONSE_BYTES);
  if(responseText===null)throw safeErr('CLOUDFLARE_OVERSIZED_RESPONSE',502);
  let data;try{data=JSON.parse(responseText)}catch{throw safeErr('CLOUDFLARE_MALFORMED_JSON',502)}
  if(data?.ok!==true||data?.inference_executed!==true||data?.provider_family!=='cloudflare'||typeof data?.text!=='string'||!data.text.trim())throw safeErr('CLOUDFLARE_INVALID_INFERENCE_PROOF',502);
  if(data?.correlation_id!==correlationId)throw safeErr('CLOUDFLARE_CORRELATION_MISMATCH',502);
  return {text:data.text,status:res.status,requestId:correlationId,model:String(data.model||'unknown'),provider:CLOUDFLARE_PROVIDER,provider_family:CLOUDFLARE_FAMILY,cognitive_execution_verified:true};
 }catch(e){
  if(e?.name==='AbortError')throw safeErr('CLOUDFLARE_TIMEOUT',504);
  throw e;
 }finally{clearTimeout(timer)}
}
export async function cloudflarePreflight({env,fetchFn=fetch}){
 try{
  const r=await cloudflareGenerate({system:'Return nonempty text only.',prompt:'Reply OK.',correlationId:'cloudflare-preflight',env,fetchFn,timeoutMs:12000});
  return {ok:true,status:200,error:null,detail:{provider:CLOUDFLARE_PROVIDER,provider_family:CLOUDFLARE_FAMILY,model:r.model,inference_executed:true}};
 }catch(e){return {ok:false,status:e?.status||null,error:String(e?.message||'CLOUDFLARE_PREFLIGHT_FAILED'),detail:null}}
}
