# Ti Penso Device Control System

Control system for Ti Penso devices that manages communication between BLE devices and the WebSocket server.

## Project Structure

```
device_control/
├── core/               # Shared constants and configurations
├── managers/          # Process and dependency managers
├── protocols/         # Communication protocols
├── scripts/          # Executable scripts
│   ├── server.py     # WebSocket server
│   └── ble_bridge.py # BLE bridge for devices
└── run.bat           # Batch script to start/stop the system
```

## Requirements

- Python 3.8 or higher
- Windows 10 or higher
- Python dependencies (installed automatically):
  - bleak (for BLE communication)
  - websockets (for WebSocket communication)
  - psutil (for process management)

## Installation

1. Clone the repository
2. Navigate to the `device_control` directory
3. Run the command to install dependencies:
   ```batch
   pip install -r requirements.txt
   ```

## Usage

The system can be started in two modes: local and remote.

### Starting the System

```batch
run.bat start [local|remote]
```

- `local`: Starts the local server and BLE bridges
- `remote`: Starts only the BLE bridges for connection to a remote server

### Stopping the System

```batch
run.bat stop [local|remote]
```

### Examples

1. Start local version:
   ```batch
   run.bat start local
   ```

2. Start remote version:
   ```batch
   run.bat start remote
   ```

3. Stop local version:
   ```batch
   run.bat stop local
   ```

4. Stop remote version:
   ```batch
   run.bat stop remote
   ```

## Main Components

### App Manager (`app_manager.py`)
- Manages application lifecycle
- Coordinates component startup and shutdown
- Verifies required dependencies

### Process Manager (`managers/process_manager.py`)
- Manages Python processes
- Starts and stops system components
- Keeps command windows open for monitoring

### BLE Bridge (`scripts/ble_bridge.py`)
- Manages communication with BLE devices
- Supports both local and remote modes
- Handles WebSocket connection with server

### Server (`scripts/server.py`)
- Manages WebSocket connections
- Coordinates communication between devices
- Handles device registration

## Configuration

Constants and configurations are defined in `core/constants.py`:

- `LOCAL_SERVER_URL`: Local server URL
- `REMOTE_SERVER_URL`: Remote server URL
- `PAIR_CODE`: Pairing code for registration
- `DEVICE_NAME_PREFIX`: Prefix for device names
- `BUTTON_CHAR_UUID`: Button characteristic UUID
- `LED_CHAR_UUID`: LED characteristic UUID

## Troubleshooting

1. **Windows closing immediately**
   - Verify Python is installed correctly
   - Check that all dependencies are installed

2. **BLE connection failed**
   - Verify devices are powered on and in range
   - Check that Bluetooth is enabled on the computer

3. **Server connection failed**
   - Verify server URL is correct
   - Check that server is running
   - Verify firewall is not blocking connections

## Development Notes

- The system is designed to be modular and easily extensible
- Components communicate through WebSocket for maximum flexibility
- Process management is centralized for better maintainability 