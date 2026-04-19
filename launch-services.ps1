$root = 'C:\Projects\radar-tracking-system'
$services = @('plot-listener-service','tracker-service','threat-assessment-service','map-viewer-service','scenario-editor-service','radar-service')
foreach ($svc in $services) {
    Write-Host "Starting $svc ..."
    Start-Process cmd -ArgumentList "/k $root\gradlew.bat :${svc}:bootRun" -WorkingDirectory $root -WindowStyle Normal
    Start-Sleep 5
}
Write-Host "All services launched."
