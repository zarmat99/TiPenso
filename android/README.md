# Ti Penso Android App

## Overview
The Ti Penso Android app is the client application that connects to ESP32-C3 devices via Bluetooth Low Energy (BLE). It allows users to discover, connect to, and interact with their Ti Penso devices.

## Features
- BLE device discovery and connection
- Real-time button state monitoring
- LED control
- Automatic reconnection
- User-friendly interface
- Background service support

## Requirements
- Android 6.0 (API level 23) or higher
- Bluetooth Low Energy support
- Location permissions (required for BLE scanning)

## Project Structure
```
android/
├── app/                    # Main application module
│   ├── src/               # Source code
│   │   ├── main/         # Main source files
│   │   └── test/         # Unit tests
│   └── docs/             # Android-specific documentation
├── build.gradle.kts       # Project build configuration
├── settings.gradle.kts    # Project settings
└── gradle.properties      # Gradle properties
```

## Setup Instructions

### Development Environment
1. Install Android Studio
2. Clone the repository
3. Open the project in Android Studio
4. Sync Gradle files
5. Build the project

### Required Permissions
Add the following permissions to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## BLE Implementation

### Service and Characteristics
- Service UUID: "12345678-1234-5678-1234-56789abcdef0"
- Button Characteristic UUID: "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0" (Notify)
- LED Characteristic UUID: "d0b39442-8be6-4d15-9610-054c3efc1c4f" (Write)

### Connection Flow
1. Start BLE scan
2. Filter for devices named "Ti Penso"
3. Connect to selected device
4. Discover services and characteristics
5. Enable notifications for button state

## Building and Running

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Running Tests
```bash
./gradlew test
```

## Debugging

### BLE Debugging
1. Enable BLE debugging in Developer Options
2. Use Android Studio's BLE debugging tools
3. Check logcat for BLE-related messages

### Common Issues
1. BLE not scanning
   - Check location permissions
   - Verify Bluetooth is enabled
   - Check if location services are enabled

2. Connection failures
   - Verify device is in range
   - Check if device is advertising
   - Restart Bluetooth

## Contributing
See [Contributing Guide](../docs/contributing.md) for details.

## License
[Add your chosen license here]

## Author
[Add your name/contact information here] 