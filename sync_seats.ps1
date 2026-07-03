# sync_seats.ps1
# Dong bo seats.csv theo tickets.csv:
# - Ghe chi la SOLD khi KHONG CON bat ky ticket AVAILABLE nao cho ghe do
# - Ghe la AVAILABLE neu co IT NHAT 1 ticket AVAILABLE (bat ky tran nao)

$ticketFile = ".\data\tickets.csv"
$seatFile   = ".\data\seats.csv"

Write-Host "[1] Dang doc tickets.csv..."

# hasAvailable[seatId] = true neu co it nhat 1 ticket AVAILABLE
$hasAvailable = @{}

Get-Content $ticketFile | ForEach-Object {
    $cols = $_ -split ","
    if ($cols.Length -ge 7) {
        $seatId = $cols[2].Trim().ToUpper()
        $status = $cols[6].Trim().ToUpper()
        if ($status -eq "AVAILABLE") {
            $hasAvailable[$seatId] = $true
        } elseif (-not $hasAvailable.ContainsKey($seatId)) {
            # Danh dau la ton tai nhung chua co AVAILABLE
            $hasAvailable[$seatId] = $false
        }
    }
}

Write-Host "[2] Tim thay $($hasAvailable.Count) ghe co trong tickets.csv"

Write-Host "[3] Dang cap nhat seats.csv..."
$lines = Get-Content $seatFile
$updated = 0

$result = foreach ($line in $lines) {
    $cols = $line -split ","
    if ($cols.Length -ge 5) {
        $seatId = $cols[0].Trim().ToUpper()
        if ($hasAvailable.ContainsKey($seatId)) {
            # Co AVAILABLE ticket -> ghe phai AVAILABLE
            # Khong co AVAILABLE ticket (tat ca SOLD/CANCELLED) -> ghe SOLD
            $newStatus = if ($hasAvailable[$seatId]) { "AVAILABLE" } else { "SOLD" }
            $oldStatus = $cols[4].Trim()
            if ($oldStatus -ne $newStatus) {
                $updated++
                $cols[4] = $newStatus
                $cols -join ","
                continue
            }
        }
    }
    $line
}

$result | Set-Content $seatFile -Encoding UTF8
Write-Host "[OK] Hoan thanh! Da cap nhat $updated dong trong seats.csv."
Write-Host ""

# Kiem tra nhanh SEAT2
Write-Host "=== Kiem tra SEAT2 ==="
Get-Content $ticketFile | Select-String "SEAT2," | ForEach-Object {
    $cols = ($_.Line -split ",")
    if ($cols.Length -ge 7) {
        Write-Host "  tickets.csv: Tran $($cols[1]) -> $($cols[6])"
    }
}
Get-Content $seatFile | Select-String "^SEAT2," | ForEach-Object {
    Write-Host "  seats.csv  : $($_.Line)"
}
