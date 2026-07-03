$lines = Get-Content 'data\seats.csv'
$total = $lines.Count - 1
$avail = ($lines | Select-String -Pattern ',AVAILABLE$').Count
$sold  = ($lines | Select-String -Pattern ',SOLD$').Count
Write-Host "Tong so ghe  : $total"
Write-Host "Con trong    : $avail"
Write-Host "Da ban (SOLD): $sold"
