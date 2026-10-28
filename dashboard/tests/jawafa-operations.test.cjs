"use strict";
const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const page = fs.readFileSync(path.join(__dirname,"../jawafa-operations.html"),"utf8");
const gate = fs.readFileSync(path.join(__dirname,"../jawafa-gate15.html"),"utf8");
const script = page.match(/<script>([\s\S]*?)<\/script>/);
test("dashboard JavaScript syntax is valid",()=>{
 assert.ok(script,"inline script exists");
 assert.doesNotThrow(()=>new vm.Script(script[1],{filename:"jawafa-operations.js"}));
});
test("required operations and accounting screens exist",()=>{
 for(const id of ["overview","checks","bank","accounting","purchasing","receiving","inventory","suppliers","staff","payables","agents","decisions","tasks","reports","evidence","integrations","data"])
  assert.ok(page.includes('id="screen-'+id+'"'),"missing "+id);
});
test("original GATE-15 command panel links to new module",()=>{
 assert.ok(gate.includes('href="./jawafa-operations.html"'));
 assert.ok(page.includes('href="./jawafa-gate15.html"'));
});
test("no privileged operations or browser-held API credentials",()=>{
 assert.ok(!/fetch\s*\(/.test(script[1]),"no network request from browser");
 assert.ok(!/localStorage|sessionStorage|indexedDB/.test(script[1]),"no sensitive persistent data");
 assert.ok(!/api\.openai\.com|sk-proj-|service_role|CREATE TABLE|UPDATE Jawafa_DB/i.test(page),"no backend secrets or financial writes");
 assert.ok(page.includes('gateVerified:false'));
 assert.ok(page.includes('PENDING_HUMAN_REVIEW'));
 assert.ok(page.includes('NOT_SENT'));
 assert.ok(page.includes('UNVERIFIED'));
});
const start = script[1].indexOf("function escCSV");
const end = script[1].indexOf("function digest");
assert.ok(start>0 && end>start);
const parser = script[1].slice(start,end);
const context = vm.createContext({});
vm.runInContext(parser,context);
test("CSV accepts escaped quotes, CRLF and Arabic headers",()=>{
 const rows=Array.from(context.csvRows('رقم الشيك,المستفيد\r\n100,"شركة ""الفطيم"""\r\n101,عميل'));
 assert.equal(rows.length,2);
 assert.equal(rows[0]["رقم الشيك"],"100");
 assert.equal(rows[0]["المستفيد"],'شركة "الفطيم"');
});
test("CSV rejects malformed and duplicate headers",()=>{
 assert.throws(()=>context.csvRows("a,a\n1,2"),/مكررة/);
 assert.throws(()=>context.csvRows('a,b\n"abc,5'),/غير مكتمل/);
 assert.throws(()=>context.csvRows("a,b\n1,2,3"),/غير متساو/);
});
test("exported CSV cells escape separators and Excel-breaking control lines",()=>{
 assert.equal(context.escCSV('A,B'),'"A,B"');
 assert.equal(context.escCSV('A"B'),'"A""B"');
 assert.equal(context.escCSV("=1"),"\'=1");
});