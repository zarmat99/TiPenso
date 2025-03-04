from aiohttp import web
import aiohttp
import json
import ssl
import os
from datetime import datetime, timedelta
import jwt

# Configurazione
SECRET_KEY = os.getenv('JWT_SECRET_KEY', 'your-secret-key-here')  # Cambiare in produzione
TOKEN_EXPIRY = timedelta(hours=24)
SSL_CERT_PATH = os.getenv('SSL_CERT_PATH', 'cert.pem')
SSL_KEY_PATH = os.getenv('SSL_KEY_PATH', 'key.pem')

class DevicePairManager:
    def __init__(self):
        self.pairs = {}  # {pair_code: {devices: [ws1, ws2]}}
        self.device_tokens = {}  # {ws: token}
        
    def generate_token(self, device_id):
        payload = {
            'device_id': device_id,
            'exp': datetime.utcnow() + TOKEN_EXPIRY
        }
        return jwt.encode(payload, SECRET_KEY, algorithm='HS256')
        
    def verify_token(self, token):
        try:
            payload = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
            return payload['device_id']
        except jwt.ExpiredSignatureError:
            return None
        except jwt.InvalidTokenError:
            return None
        
    async def register_device(self, ws, pair_code, token):
        device_id = self.verify_token(token)
        if not device_id:
            return False
            
        if pair_code not in self.pairs:
            self.pairs[pair_code] = {"devices": [ws]}
            self.device_tokens[ws] = token
            print(f"Server: Created new pair {pair_code} (Device 1)")
        elif len(self.pairs[pair_code]["devices"]) < 2:
            self.pairs[pair_code]["devices"].append(ws)
            self.device_tokens[ws] = token
            print(f"Server: Added Device 2 to pair {pair_code}")
        else:
            print(f"Server: Pair {pair_code} is full")
            return False
        return True
        
    async def forward_message(self, ws, message):
        if ws not in self.device_tokens:
            return False
            
        for pair_code, pair in self.pairs.items():
            if ws in pair["devices"]:
                if len(pair["devices"]) < 2:
                    print(f"Server: No paired device in pair {pair_code}")
                    return False
                    
                try:
                    sender_idx = pair["devices"].index(ws)
                    receiver_idx = 1 - sender_idx
                    
                    other_device = pair["devices"][receiver_idx]
                    await other_device.send_str(message)
                    
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
                    if "token" not in data:
                        # Genera un nuovo token se non fornito
                        token = pair_manager.generate_token(data.get("device_id", "unknown"))
                    else:
                        token = data["token"]
                        
                    success = await pair_manager.register_device(ws, data["pair_code"], token)
                    response = {
                        "type": "register_response",
                        "success": success,
                        "token": token if success else None
                    }
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
            if ws in pair_manager.device_tokens:
                del pair_manager.device_tokens[ws]
            print(f"Server: Device {device_num} disconnected from pair {pair_code}")
            if not pair["devices"]:
                del pair_manager.pairs[pair_code]
                print(f"Server: Removed empty pair {pair_code}")
            break
            
    return ws

app = web.Application()
app.router.add_get('/ws', websocket_handler)

if __name__ == '__main__':
    ssl_context = None
    if os.path.exists(SSL_CERT_PATH) and os.path.exists(SSL_KEY_PATH):
        ssl_context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
        ssl_context.load_cert_chain(SSL_CERT_PATH, SSL_KEY_PATH)
        print(f"Server: SSL enabled with certificates from {SSL_CERT_PATH}")
    else:
        print("Server: SSL certificates not found, running without SSL")
        
    print("Server: Starting on https://0.0.0.0:8080")
    web.run_app(app, host='0.0.0.0', port=8080, ssl_context=ssl_context, print=None) 