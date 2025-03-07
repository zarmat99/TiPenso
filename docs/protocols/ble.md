# Bluetooth Low Energy Protocol

## Overview
Ti Penso uses Bluetooth Low Energy (BLE) for wireless communication between devices. The protocol is designed to be simple, efficient, and reliable.

## Service and Characteristics

### Service
- UUID: "12345678-1234-5678-1234-56789abcdef0"
- Name: "Ti Penso Service"

### Characteristics

#### Button State Characteristic
- UUID: "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0"
- Properties: Notify
- Description: Reports button state changes
- Values:
  - "0": Button released
  - "1": Button pressed

#### LED Control Characteristic
- UUID: "d0b39442-8be6-4d15-9610-054c3efc1c4f"
- Properties: Write
- Description: Controls LED state
- Values:
  - "0": Turn LED off
  - "1": Turn LED on

## Connection Process

### Advertising
1. ESP32 device starts advertising with name "Ti Penso"
2. Device includes service UUID in advertising data
3. Device advertises continuously until connected

### Discovery
1. Android app starts BLE scan
2. App filters for devices named "Ti Penso"
3. App displays discovered devices to user

### Connection
1. User selects device to connect
2. App initiates GATT connection
3. App discovers services and characteristics
4. App enables notifications for button state

## Data Flow

### Button Press
1. User presses button on ESP32
2. ESP32 sends notification with value "1"
3. App receives notification
4. App processes button press event

### Button Release
1. User releases button on ESP32
2. ESP32 sends notification with value "0"
3. App receives notification
4. App processes button release event

### LED Control
1. App writes value "1" to LED characteristic
2. ESP32 receives write command
3. ESP32 turns on LED
4. App writes value "0" to LED characteristic
5. ESP32 receives write command
6. ESP32 turns off LED

## Error Handling

### Connection Errors
- Handle connection timeouts
- Implement reconnection logic
- Notify user of connection status

### Communication Errors
- Validate received data
- Handle notification failures
- Implement retry mechanism

## Security

### Pairing
- BLE pairing is required
- No additional encryption needed
- Simple protocol reduces attack surface

### Data Validation
- Validate all received data
- Check characteristic values
- Handle invalid data gracefully

## Performance

### Power Efficiency
- Use notifications for real-time updates
- Minimize write operations
- Optimize connection intervals

### Latency
- Target < 100ms for button press to LED
- Use low latency scan mode
- Optimize connection parameters 