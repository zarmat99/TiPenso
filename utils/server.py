from aiohttp import web
import aiohttp
import json

class DevicePairManager:
    def __init__(self):
        self.pairs = {}  # {pair_code: {devices: [ws1, ws2]}}
        
    async def register_device(self, ws, pair_code):
        if pair_code not in self.pairs:
            self.pairs[pair_code] = {"devices": [ws]}
            print(f"Server: Created new pair {pair_code} (Device 1)")
        elif len(self.pairs[pair_code]["devices"]) < 2:
            self.pairs[pair_code]["devices"].append(ws)
            print(f"Server: Added Device 2 to pair {pair_code}")
        else:
            print(f"Server: Pair {pair_code} is full")
            return False
        return True
        
    async def forward_message(self, ws, message):
        for pair_code, pair in self.pairs.items():
            if ws in pair["devices"]:
                if len(pair["devices"]) < 2:
                    print(f"Server: No paired device in pair {pair_code}")
                    return False
                    
                try:
                    # Determina quale device è il mittente
                    sender_idx = pair["devices"].index(ws)
                    receiver_idx = 1 - sender_idx  # Se sender è 0, receiver è 1 e viceversa
                    
                    other_device = pair["devices"][receiver_idx]
                    await other_device.send_str(message)
                    
                    # Estrai lo stato del pulsante dal messaggio
                    data = json.loads(message)
                    state = "pressed" if data["state"] == "1" else "released"
                    print(f"Server: Device {sender_idx + 1} {state} -> Device {receiver_idx + 1}")
                    return True
                except Exception as e:
                    print(f"Server: Error forwarding message in pair {pair_code}")
                    return False
                    
        print("Server: No pair found for device")
        return False

pair_manager = DevicePairManager()

async def websocket_handler(request):
    ws = web.WebSocketResponse()
    await ws.prepare(request)
    
    print("Server: New connection")
    
    async for msg in ws:
        if msg.type == aiohttp.WSMsgType.TEXT:
            try:
                data = json.loads(msg.data)
                
                if data["type"] == "register":
                    success = await pair_manager.register_device(ws, data["pair_code"])
                    response = {"type": "register_response", "success": success}
                    await ws.send_str(json.dumps(response))
                    
                elif data["type"] == "button_state":
                    success = await pair_manager.forward_message(ws, msg.data)
                    if not success:
                        error_msg = {
                            "type": "error",
                            "message": "No paired device available"
                        }
                        await ws.send_str(json.dumps(error_msg))
                    
            except json.JSONDecodeError:
                print("Server: Invalid message format")
                
        elif msg.type == aiohttp.WSMsgType.ERROR:
            print("Server: WebSocket error")
    
    # Cleanup alla disconnessione
    for pair_code, pair in pair_manager.pairs.items():
        if ws in pair["devices"]:
            device_num = pair["devices"].index(ws) + 1
            pair["devices"].remove(ws)
            print(f"Server: Device {device_num} disconnected from pair {pair_code}")
            if not pair["devices"]:
                del pair_manager.pairs[pair_code]
                print(f"Server: Removed empty pair {pair_code}")
            break
            
    return ws

app = web.Application()
app.router.add_get('/ws', websocket_handler)

if __name__ == '__main__':
    print("Server: Starting on http://0.0.0.0:8080")
    web.run_app(app, host='0.0.0.0', port=8080, print=None)  # print=None rimuove il log di default di aiohttp 