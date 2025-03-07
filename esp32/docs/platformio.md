# Ti Penso PlatformIO Project

This document describes the embedded part of the Ti Penso project, implemented on ESP32-C3 using PlatformIO.

## Project Structure

```
Embedded/
├── src/               # Source code
│   └── main.cpp      # Main file
├── include/          # Header files
├── lib/             # Custom libraries
├── test/            # Unit tests
├── platformio.ini   # PlatformIO configuration
├── esp32.py         # Deployment script
└── esp32.bat        # Batch file for deployment
```

## PlatformIO Configuration

The project is configured for ESP32-C3 with the following settings in `platformio.ini`:

```ini
[env:esp32-c3-devkitm-1]
platform = espressif32
board = esp32-c3-devkitm-1
framework = arduino
monitor_speed = 115200
upload_port = COM18
upload_protocol = esptool
board_build.f_flash = 40000000L
board_build.mcu = esp32c3
```

### Main Features

- **CPU**: ESP32-C3 (RISC-V)
- **Frequency**: 80MHz
- **Flash**: 4MB
- **RAM**: 400KB
- **USB**: CDC support
- **BLE**: Bluetooth Low Energy 5.0

## BLE Features

### Services and Characteristics

1. **Main Service**
   - UUID: `12345678-1234-5678-1234-56789abcdef0`

2. **Characteristics**
   - **Button** (Notify)
     - UUID: `f3d9e507-5fbe-48c6-9f07-0173f5fae9b0`
     - Sends button state (0/1)
   
   - **LED** (Write)
     - UUID: `d0b39442-8be6-4d15-9610-054c3efc1c4f`
     - Receives LED commands (0/1)

### Behavior

1. **Connection**
   - Device name: "Ti Penso"
   - Automatic advertising
   - Automatic reconnection

2. **Button**
   - Notifies when pressed (1)
   - Notifies when released (0)
   - Integrated debounce

3. **LED**
   - Turns on with command "1"
   - Turns off with command "0"

## Pin Mapping

- **BUTTON_PIN**: 0 (GPIO0)
- **LED_PIN**: 2 (GPIO2)

## Optimizations

1. **Power Consumption**
   - CPU at 80MHz
   - 100ms delay in main loop
   - Estimated consumption: ~87mA

2. **Performance**
   - BLE optimized for low latency
   - Efficient notification handling
   - Hardware button debounce

## Deployment

### Requirements

- PlatformIO installed
- ESP32-C3 connected via USB
- Correct COM port configured

### Procedure

1. **Compilation**
   ```bash
   pio run
   ```

2. **Upload**
   ```bash
   pio run --target upload
   ```

3. **Monitor**
   ```bash
   pio device monitor
   ```

### Deployment Script

The project includes a Python script (`esp32.py`) to automate deployment:

```bash
python esp32.py all COMx  # Replace COMx with correct port
```

## Debug

### Serial Output

- Speed: 115200 baud
- Filters: esp32_exception_decoder, default
- Debug enabled (DEBUG = 1)

### Debug Messages

1. **Initialization**
   - "Start"
   - "BLE ready, waiting for connections..."

2. **Connection**
   - "Device connected!"
   - "Initial state -> value 0 sent!"

3. **Button**
   - "Button pressed -> value 1 sent!"
   - "Button released -> value 0 sent!"

4. **LED**
   - "Command: Turn on LED!"
   - "Command: Turn off LED!"

## Troubleshooting

1. **Device not detected**
   - Check USB connection
   - Verify COM port in Device Manager
   - Try a different USB cable

2. **Upload failed**
   - Put board in download mode
   - Verify correct COM port
   - Check USB drivers

3. **BLE not working**
   - Check power supply
   - Verify connections
   - Restart device

## Development Notes

- Uses Arduino framework for ESP32
- Supports USB CDC debugging
- Implements efficient BLE notification system
- Handles errors robustly 