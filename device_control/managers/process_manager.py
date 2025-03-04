"""Process management for device control system."""

import os
import sys
import subprocess
import time
from typing import Dict, List, Optional
from core.constants import (
    SERVER_SCRIPT,
    BLE_BRIDGE_SCRIPT,
    DEVICE_1_NAME,
    DEVICE_2_NAME,
    SERVER_PORT,
    BLE_BRIDGE_PORT_1,
    BLE_BRIDGE_PORT_2,
    PYTHON_EXECUTABLE,
    PROCESS_TIMEOUT,
    BRIDGE_START_DELAY,
    SERVER_START_DELAY
)

import psutil

class ProcessManager:
    """Manages Python processes for the application."""
    
    @staticmethod
    def find_python_processes(script_name: str) -> List[psutil.Process]:
        """Find Python processes running a specific script.
        
        Args:
            script_name: Name of the script to search for
            
        Returns:
            List of matching processes
        """
        processes = []
        for proc in psutil.process_iter(['pid', 'name', 'cmdline']):
            try:
                if (proc.info['name'] == PYTHON_EXECUTABLE and 
                    script_name in ' '.join(proc.info['cmdline'])):
                    processes.append(proc)
            except (psutil.NoSuchProcess, psutil.AccessDenied):
                pass
        return processes

    @staticmethod
    def stop_processes(script_name: str) -> None:
        """Stop all Python processes running a specific script.
        
        Args:
            script_name: Name of the script to stop
        """
        processes = ProcessManager.find_python_processes(script_name)
        for proc in processes:
            try:
                print(f"Stopping process {proc.info['pid']} ({script_name})...")
                proc.terminate()
                proc.wait(timeout=PROCESS_TIMEOUT)
            except psutil.TimeoutExpired:
                proc.kill()
            except psutil.NoSuchProcess:
                pass

    @staticmethod
    def is_process_running(script_name: str) -> bool:
        """Check if a specific script is currently running.
        
        Args:
            script_name: Name of the script to check
            
        Returns:
            True if the script is running, False otherwise
        """
        return len(ProcessManager.find_python_processes(script_name)) > 0

    @staticmethod
    def start_process(script: str, args: Optional[List[str]] = None) -> None:
        """Start a Python process with optional arguments.
        
        Args:
            script: Name of the script to run
            args: Optional list of arguments to pass to the script
        """
        # Get absolute path of the script
        script_path = os.path.abspath(script)
        
        # Build command with absolute paths
        cmd = [sys.executable, script_path]
        if args:
            cmd.extend(args)
            
        # Start process with new window
        subprocess.Popen(
            ['cmd', '/k'] + cmd,
            shell=False,
            creationflags=subprocess.CREATE_NEW_CONSOLE
        )

    @staticmethod
    def start_local() -> None:
        """Start the local version of the application."""
        print("Starting local version...")
        
        # Stop any existing processes first
        print("Stopping any existing processes...")
        ProcessManager.stop_processes(SERVER_SCRIPT)
        ProcessManager.stop_processes(BLE_BRIDGE_SCRIPT)
        time.sleep(2)  # Wait for processes to fully stop
        
        # Start server
        print("Starting server...")
        ProcessManager.start_process(SERVER_SCRIPT)
        time.sleep(SERVER_START_DELAY)
        
        # Start bridges
        for device_num in [1, 2]:
            print(f"Starting Device {device_num}...")
            ProcessManager.start_process(BLE_BRIDGE_SCRIPT, [str(device_num), "local"])
            time.sleep(BRIDGE_START_DELAY)

    @staticmethod
    def start_remote() -> None:
        """Start the remote version of the application."""
        print("Starting remote version...")
        
        # Start bridges
        for device_num in [1, 2]:
            print(f"Starting Device {device_num} Remote...")
            ProcessManager.start_process(BLE_BRIDGE_SCRIPT, [str(device_num), "remote"])
            time.sleep(BRIDGE_START_DELAY)

    @staticmethod
    def stop_local() -> None:
        """Stop the local version of the application."""
        print("Stopping local version...")
        ProcessManager.stop_processes(SERVER_SCRIPT)
        ProcessManager.stop_processes(BLE_BRIDGE_SCRIPT)

    @staticmethod
    def stop_remote() -> None:
        """Stop the remote version of the application."""
        print("Stopping remote version...")
        ProcessManager.stop_processes(BLE_BRIDGE_SCRIPT) 