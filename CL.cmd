@echo off
echo Counting lines of code...
"cloc.exe" --force-lang=C++,adr central channel common proxy realm 
pause