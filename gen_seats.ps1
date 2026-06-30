$sections = @('SEC1','SEC2','SEC3','SEC4','SEC5','SEC6','SEC7','SEC8','SEC9','SEC10','SEC11','SEC12','SEC13','SEC14','SEC15','SEC16','SEC17','SEC18','SEC19','SEC20')
$ROWS = 10
$SEATS = 100
$outFile = 'd:\github\NHOM_04_LAB211_TicketBooking\data\seats.csv'

$sb = New-Object System.Text.StringBuilder
[void]$sb.AppendLine('seatId,sectionId,row,number,status')
$seat = 1
foreach ($sec in $sections) {
    for ($row = 1; $row -le $ROWS; $row++) {
        for ($num = 1; $num -le $SEATS; $num++) {
            [void]$sb.AppendLine("SEAT${seat},${sec},${row},${num},AVAILABLE")
            $seat++
        }
    }
}
[System.IO.File]::WriteAllText($outFile, $sb.ToString())
Write-Host ("Done! " + ($seat - 1) + " seats across " + $sections.Count + " sections (1000 each).")
