# 1. Doc tickets.csv, tim cac seatId co status = SOLD
$soldSeats = @{}
Get-Content 'data\tickets.csv' | ForEach-Object {
    $parts = $_ -split ','
    if ($parts.Length -ge 7) {
        $seatId = $parts[2].Trim()
        $status = $parts[6].Trim()
        if ($status -eq 'SOLD') {
            $soldSeats[$seatId] = $true
        }
    }
}
Write-Host "Found $($soldSeats.Count) SOLD seats in tickets.csv"

# 2. Doc seats.csv, cap nhat cot status neu seatId nam trong danh sach SOLD
$lines = Get-Content 'data\seats.csv'
$updated = 0
$newLines = $lines | ForEach-Object {
    $parts = $_ -split ','
    if ($parts.Length -ge 5 -and $soldSeats.ContainsKey($parts[0].Trim())) {
        $parts[4] = 'SOLD'
        $updated++
        $parts -join ','
    } else {
        $_
    }
}
Write-Host "Updated $updated seats to SOLD in seats.csv"

# 3. Ghi lai file
$newLines | Set-Content 'data\seats.csv' -Encoding UTF8
Write-Host "Done!"
