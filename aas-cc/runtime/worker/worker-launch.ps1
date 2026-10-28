$ErrorActionPreference="Continue"
$log="C:\AAS\local-ai-poc\worker-run.log"
try {
    & "C:\Program Files\nodejs\node.exe" "C:\AAS\local-ai-poc\aas-worker-once.js" *>> $log
    exit $LASTEXITCODE
} catch {
    $_.Exception.Message | Out-File -FilePath $log -Append
    exit 1
}
