"""BLE communication module for device control."""

import asyncio
from bleak import BleakClient, BleakScanner
from typing import Optional, Callable
from ..core.constants import (
    BUTTON_CHAR_UUID,
    LED_CHAR_UUID,
    DEVICE_NAME_PREFIX,
    MSG_TYPE_BUTTON_STATE
)

class BLEManager:
    """Manages BLE communication with the device."""
    
    def __init__(self, device_number: int):
        """Initialize BLE manager.
        
        Args:
            device_number: Unique identifier for this device instance
        """
        self.ble_client: Optional[BleakClient] = None
        self.device_address: Optional[str] = None
        self.device_number = device_number
        self.button_callback: Optional[Callable] = None
        
    async def scan_for_device(self) -> bool:
        """Scan for the target device.
        
        Returns:
            True if device is found, False otherwise
        """
        print(f"Device {self.device_number}: Scanning...")
        devices = await BleakScanner.discover()
        for device in devices:
            if device.name and DEVICE_NAME_PREFIX in device.name:
                self.device_address = device.address
                print(f"Device {self.device_number}: Found {DEVICE_NAME_PREFIX}")
                return True
        return False

    async def connect(self) -> bool:
        """Connect to the device.
        
        Returns:
            True if connection successful, False otherwise
        """
        if not self.device_address:
            return False
            
        try:
            self.ble_client = BleakClient(self.device_address)
            await self.ble_client.connect()
            print(f"Device {self.device_number}: Connected")
            
            # Set up button notification handler
            await self.ble_client.start_notify(
                BUTTON_CHAR_UUID,
                self._handle_button_notification
            )
            
            return True
        except Exception as e:
            print(f"Device {self.device_number}: Connection failed: {str(e)}")
            return False

    async def disconnect(self) -> None:
        """Disconnect from the device."""
        if self.ble_client and self.ble_client.is_connected:
            await self.ble_client.disconnect()
            print(f"Device {self.device_number}: Disconnected")

    async def set_led_state(self, state: bool) -> bool:
        """Set the LED state on the device.
        
        Args:
            state: True to turn LED on, False to turn off
            
        Returns:
            True if command successful, False otherwise
        """
        if not self.ble_client or not self.ble_client.is_connected:
            return False
            
        try:
            await self.ble_client.write_gatt_char(
                LED_CHAR_UUID,
                bytes([1 if state else 0])
            )
            print(f"Device {self.device_number}: LED {'ON' if state else 'OFF'}")
            return True
        except Exception as e:
            print(f"Device {self.device_number}: LED command failed: {str(e)}")
            return False

    def set_button_callback(self, callback: Callable) -> None:
        """Set callback for button notifications.
        
        Args:
            callback: Function to call when button state changes
        """
        self.button_callback = callback

    def _handle_button_notification(self, sender: int, data: bytearray) -> None:
        """Handle button state change notifications.
        
        Args:
            sender: GATT characteristic handle
            data: Button state data
        """
        if self.button_callback and len(data) > 0:
            state = bool(data[0])
            print(f"Device {self.device_number}: Button {'pressed' if state else 'released'}")
            self.button_callback(state) 