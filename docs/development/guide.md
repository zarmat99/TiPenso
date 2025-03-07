# Development Guide

## Development Environment Setup

### Required Tools
1. Android Studio (for Android app development)
2. PlatformIO (for ESP32 development)
3. Python 3.x (for device_control utility)
4. Git (for version control)

### Android Development
1. Install Android Studio
2. Install required SDK versions
3. Configure Android Studio for BLE development
4. See [Android Setup Guide](android/docs/setup.md) for details

### ESP32 Development
1. Install PlatformIO
2. Install Arduino CLI
3. Configure USB drivers
4. See [ESP32 Setup Guide](esp32/README.md) for details

### Device Control Utility
1. Install Python 3.x
2. Install required Python packages
3. See [Device Control Guide](utils/device_control/docs/README.md) for details

## Project Structure

### Android App
```
android/
├── app/              # Main application module
│   ├── src/         # Source code
│   └── docs/        # Android-specific documentation
└── docs/            # Additional documentation
```

### ESP32 Firmware
```
esp32/
├── src/             # Source code
├── include/         # Header files
├── lib/             # Libraries
└── docs/            # ESP32-specific documentation
```

### Device Control Utility
```
utils/device_control/
├── scripts/         # Python scripts
├── core/            # Core functionality
└── docs/            # Utility-specific documentation
```

## Development Workflow

### 1. Getting Started
1. Clone the repository
2. Set up development environment
3. Build and test each component

### 2. Development Process
1. Create feature branch
2. Implement changes
3. Test changes
4. Submit pull request
5. Code review
6. Merge changes

### 3. Testing
1. Unit tests
2. Integration tests
3. End-to-end tests
4. See [Testing Guide](testing.md) for details

## Code Style

### Android (Kotlin)
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused

### ESP32 (C++)
- Follow C++ coding conventions
- Use clear variable names
- Document public APIs
- Keep functions focused

### Python
- Follow PEP 8
- Use type hints
- Add docstrings
- Keep functions focused

## Debugging

### Android App
- Use Android Studio debugger
- Enable BLE debugging
- Check logcat for errors
- Use breakpoints

### ESP32
- Use Serial monitor
- Enable debug mode
- Check error messages
- Use LED indicators

### Device Control
- Use Python debugger
- Enable verbose logging
- Check error messages
- Use print statements

## Common Issues

### BLE Connection Issues
1. Check Bluetooth permissions
2. Verify device is advertising
3. Check service UUIDs
4. Monitor connection state

### Build Issues
1. Check dependencies
2. Verify SDK versions
3. Clean and rebuild
4. Check error messages

### Testing Issues
1. Check test environment
2. Verify test data
3. Monitor test logs
4. Check test coverage

## Best Practices

### Code Organization
- Keep related code together
- Use clear file names
- Maintain consistent structure
- Document public APIs

### Error Handling
- Handle all error cases
- Provide meaningful messages
- Log errors appropriately
- Implement recovery logic

### Performance
- Optimize BLE operations
- Minimize power usage
- Reduce latency
- Handle background tasks

## Contributing
See [Contributing Guide](contributing.md) for details. 