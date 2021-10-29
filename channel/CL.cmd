@echo off
echo Counting lines of code...
"cloc.exe" --force-lang=C++,adr src scripts data config
pause