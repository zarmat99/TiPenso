# Ti Penso - A Love Connection Device

## Overview
"Ti Penso" (Italian for "I'm thinking of you") is a romantic IoT device designed for couples. It creates a tangible connection between two people, allowing them to share moments of thinking about each other through a simple gesture.

When one person presses their button, it lights up the LED on their partner's device, creating an intimate way to say "I'm thinking of you right now" without words, across any distance.

## Project Structure
```
TiPenso/
├── android/           # Android app (main client)
├── esp32/            # ESP32 firmware
├── utils/            # Utility tools
│   └── device_control/  # Legacy prototype (for reference)
├── docs/             # Project documentation
│   ├── architecture/ # System architecture
│   ├── setup/        # Setup guides
│   ├── protocols/    # Communication protocols
│   └── development/  # Development guides
└── README.md         # This file
```

## Quick Start

### Hardware Setup
1. Connect the ESP32-C3 board
2. Flash the firmware using the ESP32 deployment script
3. See [ESP32 Setup Guide](esp32/README.md) for details

### Android App Setup
1. Open the Android project in Android Studio
2. Build and run the app
3. See [Android Setup Guide](android/docs/setup.md) for details

## Documentation

### General Documentation
- [System Architecture](docs/architecture/overview.md)
- [Communication Protocols](docs/protocols/ble.md)
- [Development Guide](docs/development/guide.md)

### Component Documentation
- [Android App](android/README.md)
- [ESP32 Firmware](esp32/README.md)
- [Legacy Device Control](utils/device_control/README.md)

## Features
- BLE connectivity with notify and write characteristics
- Button state monitoring and notifications
- LED control through BLE commands
- Android app for easy device management
- Power-efficient design (80MHz CPU frequency)
- Debug mode for development

## Contributing
See [Contributing Guide](docs/development/contributing.md) for details.

## License
[Add your chosen license here]

## Author
[Add your name/contact information here] 