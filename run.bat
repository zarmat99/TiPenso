@echo off
echo Compilazione in corso...
pio run
if %errorlevel% neq 0 (
    echo Errore durante la compilazione!
    pause
    exit /b
)

echo Caricamento del firmware...
pio run --target upload
if %errorlevel% neq 0 (
    echo Errore durante il caricamento!
    pause
    exit /b
)

echo Avvio del monitor seriale...
arduino-cli monitor --port COM15 --config 115200

rem you can use also:
rem pio device monitor
rem pause
