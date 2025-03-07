# System Architecture

## Overview
Ti Penso is a distributed system composed of two main components:
1. ESP32-C3 devices (hardware)
2. Android app (client)

The system was originally prototyped using a WebSocket-based architecture (device_control), but has been migrated to a direct BLE connection model using the Android app.

## Component Architecture

### ESP32-C3 Device
The ESP32-C3 is the core hardware component that:
- Provides BLE connectivity
- Monitors button state
- Controls LED output
- Implements the BLE service and characteristics

### Android App
The Android app is the main client that:
- Discovers and connects to ESP32 devices
- Manages device connections
- Provides user interface for interaction
- Implements the BLE client protocol
- Handles direct communication between devices

### Legacy Device Control System
The device_control system was the original prototype that:
- Used WebSocket for remote communication
- Connected ESP32 devices to a server
- Supported both local and remote modes
- Is now being replaced by the Android app

## Communication Protocol

### BLE Service
- Service UUID: "12345678-1234-5678-1234-56789abcdef0"
- Button Characteristic UUID: "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0" (Notify)
- LED Characteristic UUID: "d0b39442-8be6-4d15-9610-054c3efc1c4f" (Write)

### Message Protocol
1. Button State Messages
   - "0": Button released
   - "1": Button pressed

2. LED Control Messages
   - "0": Turn LED off
   - "1": Turn LED on

## System Flow

### Connection Flow
1. ESP32 device starts advertising
2. Android app discovers device
3. User initiates connection
4. App connects to device
5. App enables notifications for button state

### Interaction Flow
1. User presses button on device A
2. Device A sends notification
3. App receives notification
4. App sends command to device B
5. Device B turns on LED

## Security Considerations
- BLE pairing is required for connection
- No sensitive data is transmitted
- Simple protocol reduces attack surface

## Performance Considerations
- ESP32 runs at 80MHz for power efficiency
- BLE notifications for real-time updates
- Minimal power consumption design

## Future Improvements
- Add encryption for messages
- Implement OTA updates
- Add battery monitoring
- Improve connection reliability
- Add remote connectivity features (future) 