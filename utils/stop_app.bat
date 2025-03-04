@echo off
echo Stopping Ti Penso Application...

:: Trova e termina i processi Python che eseguono i nostri script
for /f "tokens=2" %%a in ('tasklist ^| findstr "python.exe"') do (
    wmic process where "ProcessId=%%a" get CommandLine 2>nul | findstr "server.py" >nul
    if not errorlevel 1 (
        echo Chiusura server...
        taskkill /PID %%a /F
    )
    wmic process where "ProcessId=%%a" get CommandLine 2>nul | findstr "ble_bridge.py" >nul
    if not errorlevel 1 (
        echo Chiusura bridge...
        taskkill /PID %%a /F
    )
)

echo Sistema Ti Penso arrestato!
timeout /t 2 /nobreak 