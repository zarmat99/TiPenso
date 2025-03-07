"""Shared constants for the device control system."""

import os

# BLE Constants
BUTTON_CHAR_UUID = "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0"
LED_CHAR_UUID = "d0b39442-8be6-4d15-9610-054c3efc1c4f"
DEVICE_NAME_PREFIX = "Ti Penso"

# Server Configuration
LOCAL_SERVER_URL = "ws://localhost:8080/ws"
REMOTE_SERVER_URL = "ws://your-remote-server:8080/ws"
PAIR_CODE = "12345"

# Process Management
PYTHON_EXECUTABLE = "python.exe"
PROCESS_TIMEOUT = 3  # seconds
BRIDGE_START_DELAY = 2  # seconds between bridge starts
SERVER_START_DELAY = 3  # seconds to wait for server startup

# Script Names and Paths
SCRIPT_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "scripts")
SERVER_SCRIPT = os.path.join(SCRIPT_DIR, "server.py")
BLE_BRIDGE_SCRIPT = os.path.join(SCRIPT_DIR, "ble_bridge.py")

# Device Names
DEVICE_1_NAME = f"{DEVICE_NAME_PREFIX} 1"
DEVICE_2_NAME = f"{DEVICE_NAME_PREFIX} 2"

# Ports
SERVER_PORT = 8080
BLE_BRIDGE_PORT_1 = 8081
BLE_BRIDGE_PORT_2 = 8082

# Message Types
MSG_TYPE_REGISTER = "register"
MSG_TYPE_REGISTER_RESPONSE = "register_response"
MSG_TYPE_BUTTON_STATE = "button_state"
MSG_TYPE_ERROR = "error" 