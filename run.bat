@echo off
chcp 65001 >nul
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
java -cp out Main
