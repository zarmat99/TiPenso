@echo off
echo Starting Ti Penso Application...

:: Verifica se Python e' installato
python --version >nul 2>&1
if errorlevel 1 (
    echo Python non trovato! Installare Python 3.x
    pause
    exit /b
)

:: Verifica se i moduli necessari sono installati
python -c "import bleak" >nul 2>&1
if errorlevel 1 (
    echo Installazione dipendenze...
    pip install bleak websockets aiohttp
)

:: Avvia il server in background
start "Ti Penso Server" cmd /c "python server.py"

:: Attende 3 secondi per assicurarsi che il server sia attivo
echo Avvio server...
timeout /t 3 /nobreak

:: Avvia il primo bridge
echo Avvio Device 1...
start "Ti Penso Device 1" cmd /c "python ble_bridge.py 1"

:: Attende 2 secondi tra i bridge
timeout /t 2 /nobreak

:: Avvia il secondo bridge
echo Avvio Device 2...
start "Ti Penso Device 2" cmd /c "python ble_bridge.py 2"

echo Sistema Ti Penso avviato!
echo Per chiudere, usa stop_app.bat 