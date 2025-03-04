# Ti Penso - A Love Connection Device

## Overview
"Ti Penso" (Italian for "I'm thinking of you") is a romantic IoT device designed for couples. It creates a tangible connection between two people, allowing them to share moments of thinking about each other through a simple gesture.

When one person presses their button, it lights up the LED on their partner's device, creating an intimate way to say "I'm thinking of you right now" without words, across any distance.

### How It Works
1. Each person in the couple has their own ESP32-C3 device
2. When you think about your partner, simply press the button
3. Your partner's device will immediately light up with an LED
4. They know you're thinking about them at that exact moment
5. They can respond by pressing their button too, lighting up your device

This creates a simple but meaningful way to maintain emotional connection throughout the day, perfect for:
- Long distance relationships
- Couples living in different time zones
- Partners who are temporarily separated
- Any two people who want to share moments of connection

## Features

- BLE connectivity with notify and write characteristics
- Button state monitoring and notifications
- LED control through BLE commands
- Debug mode for development
- Power-efficient design (80MHz CPU frequency)
- Python-based deployment tool

## Hardware Requirements

- ESP32-C3 development board
- Push button (connected to GPIO 0)
- LED (connected to GPIO 2)
- USB cable for programming and debugging

## Pin Configuration

- Button Pin: GPIO 0
- LED Pin: GPIO 2

## BLE Specifications

### Device Name
- "Ti Penso"

### Service & Characteristics
- Service UUID: "12345678-1234-5678-1234-56789abcdef0"
- Button Characteristic UUID: "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0" (Notify)
- Receive Characteristic UUID: "d0b39442-8be6-4d15-9610-054c3efc1c4f" (Write)

### Protocol

#### Button States
- "0": Button released
- "1": Button pressed

#### LED Control Commands
- "0": Turn LED off
- "1": Turn LED on

## Development Tools

### ESP32 Deployment Script (esp32.py)

A Python utility script for managing the ESP32 development workflow.

#### Commands
```bash
python esp32.py [command] [port]
```

Available commands:
- `build`: Compile the project
- `upload`: Upload firmware to ESP32
- `monitor`: Start serial monitor
- `all`: Execute build, upload, and monitor in sequence
- `list`: Show available COM ports
- `help`: Display help information

Examples:
```bash
python esp32.py build              # Only compile
python esp32.py upload COM3        # Upload to COM3
python esp32.py monitor COM5       # Monitor COM5
python esp32.py all COM7           # Build, upload, and monitor on COM7
python esp32.py list              # List available ports
```

## Debug Mode

Debug messages can be enabled/disabled by setting the DEBUG macro in main.cpp:
```cpp
#define DEBUG 1  // Set to 0 to disable debug messages
```

When enabled, debug messages are sent through Serial at 115200 baud rate.

## Building and Uploading

1. Clone this repository
2. Install PlatformIO and Arduino CLI
3. Connect your ESP32-C3 board
4. Run the deployment script:
   ```bash
   python esp32.py all COMx  # Replace COMx with your port
   ```

## Troubleshooting

1. If the board is not detected:
   - Check USB connection
   - Verify COM port in Device Manager
   - Try a different USB cable

2. If upload fails:
   - Put the board in download mode
   - Check if the correct COM port is selected
   - Verify USB drivers are installed

## Power Consumption

- Operating at 80MHz CPU frequency
- ~87mA current draw with 100ms delay in main loop

## Contributing

Feel free to submit issues and enhancement requests!

## License

[Add your chosen license here]

## Author

[Add your name/contact information here] 