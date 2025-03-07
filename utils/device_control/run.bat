@echo off
setlocal enabledelayedexpansion

:: Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo Python is not installed or not in PATH
    exit /b 1
)

:: Check arguments
if "%1"=="" (
    echo Usage: ti_penso.bat [start|stop] [local|remote]
    echo Example: ti_penso.bat start local
    echo          ti_penso.bat stop remote
    exit /b 1
)

set ACTION=%1
set VERSION=%2

:: Validate action
if not "%ACTION%"=="start" if not "%ACTION%"=="stop" (
    echo Invalid action. Use 'start' or 'stop'
    exit /b 1
)

:: Set default version to local if not specified
if "%VERSION%"=="" set VERSION=local

:: Validate version
if not "%VERSION%"=="local" if not "%VERSION%"=="remote" (
    echo Invalid version. Use 'local' or 'remote'
    exit /b 1
)

:: Add current directory and parent directory to PYTHONPATH
set PYTHONPATH=%CD%;%CD%\..;%PYTHONPATH%

:: Execute the command
echo Running Ti Penso system %ACTION% %VERSION%...
python -m app_manager %ACTION% %VERSION%

if errorlevel 1 (
    echo Error executing command
    exit /b 1
)

echo Command completed successfully
exit /b 0 