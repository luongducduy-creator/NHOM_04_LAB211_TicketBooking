$inputFile = 'd:\github\NHOM_04_LAB211_TicketBooking\data\fans.csv'
$csv = Import-Csv $inputFile
foreach ($row in $csv) {
    $row.password = 'password123'
}
$csv | Export-Csv -Path $inputFile -NoTypeInformation -Encoding UTF8
