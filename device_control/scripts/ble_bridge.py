import asyncio
from bleak import BleakClient, BleakScanner
import websockets
import json
import sys
import os

# Add the parent directory to the Python path to import constants
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from core.constants import (
    BUTTON_CHAR_UUID,
    LED_CHAR_UUID,
    LOCAL_SERVER_URL,
    REMOTE_SERVER_URL,
    PAIR_CODE
)

class BLEBridge:
    def __init__(self, device_number, version="local"):
        self.ble_client = None
        self.ws_client = None
        self.device_address = None
        self.device_number = device_number
        self.version = version
        self.server_url = REMOTE_SERVER_URL if version == "remote" else LOCAL_SERVER_URL
        
    async def scan_for_device(self):
        print(f"Device {self.device_number}: Scanning...")
        devices = await BleakScanner.discover()
        for device in devices:
            if device.name and "Ti Penso" in device.name:
                self.device_address = device.address
                print(f"Device {self.device_number}: Found Ti Penso")
                return True
        return False
        
    async def connect_ble(self):
        if not self.device_address:
            if not await self.scan_for_device():
                print(f"Device {self.device_number}: Not found")
                return False
                
        self.ble_client = BleakClient(self.device_address)
        
        try:
            await self.ble_client.connect()
            await self.ble_client.start_notify(
                BUTTON_CHAR_UUID,
                self.handle_button_notification
            )
            print(f"Device {self.device_number}: Connected")
            return True
            
        except Exception as e:
            print(f"Device {self.device_number}: Connection failed")
            return False
            
    async def handle_button_notification(self, characteristic, data):
        if self.ws_client and self.ws_client.open:
            # Converti il bytearray in stringa e prendi il primo carattere
            value = data.decode('utf-8')[0]
            
            message = {
                "type": "button_state",
                "state": value
            }
            await self.ws_client.send(json.dumps(message))
            print(f"Device {self.device_number}: Button {'pressed' if value == '1' else 'released'} (raw: {data})")
            
    async def connect_server(self):
        try:
            self.ws_client = await websockets.connect(self.server_url)
            await self.ws_client.send(json.dumps({
                "type": "register",
                "pair_code": PAIR_CODE
            }))
            
            response = await self.ws_client.recv()
            data = json.loads(response)
            if data["type"] == "register_response" and data["success"]:
                print(f"Device {self.device_number}: Connected to server")
                return True
            else:
                print(f"Device {self.device_number}: Server registration failed")
                return False
                
        except Exception as e:
            print(f"Device {self.device_number}: Server connection failed")
            return False
            
    async def handle_server_messages(self):
        while True:
            try:
                message = await self.ws_client.recv()
                data = json.loads(message)
                
                if data["type"] == "button_state":
                    state = data["state"]
                    # Invia il valore come stringa codificata in bytes
                    value = state.encode('utf-8')
                    await self.ble_client.write_gatt_char(LED_CHAR_UUID, value)
                    print(f"Device {self.device_number}: LED {'ON' if state == '1' else 'OFF'} (sent: {value})")
                elif data["type"] == "error":
                    print(f"Device {self.device_number}: {data['message']}")
                    
            except Exception as e:
                print(f"Device {self.device_number}: Connection lost")
                break
                
    async def run(self):
        if not await self.connect_ble():
            return
            
        if not await self.connect_server():
            await self.ble_client.disconnect()
            return
            
        try:
            await self.handle_server_messages()
        finally:
            if self.ble_client:
                await self.ble_client.disconnect()
            if self.ws_client:
                await self.ws_client.close()

async def main():
    # Prendi il numero del device e la versione da riga di comando
    device_number = int(sys.argv[1]) if len(sys.argv) > 1 else 1
    version = sys.argv[2] if len(sys.argv) > 2 else "local"
    
    if device_number not in [1, 2]:
        print("Error: Device number must be 1 or 2")
        return
        
    if version not in ["local", "remote"]:
        print("Error: Version must be 'local' or 'remote'")
        return
        
    bridge = BLEBridge(device_number, version)
    await bridge.run()

if __name__ == "__main__":
    asyncio.run(main()) 