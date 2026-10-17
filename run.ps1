# Cấu hình UTF-8 toàn diện cho PowerShell và Console
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 > $null

# Biên dịch toàn bộ mã nguồn Java
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Chạy chương trình
java -cp out Main
