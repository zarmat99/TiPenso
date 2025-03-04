"""WebSocket communication module for device control."""

import asyncio
import websockets
import json
from typing import Optional, Callable, Dict, Any
from ..core.constants import (
    LOCAL_SERVER_URL,
    REMOTE_SERVER_URL,
    PAIR_CODE,
    MSG_TYPE_REGISTER,
    MSG_TYPE_REGISTER_RESPONSE,
    MSG_TYPE_BUTTON_STATE,
    MSG_TYPE_ERROR
)

class WebSocketManager:
    """Manages WebSocket communication for device control."""
    
    def __init__(self, is_remote: bool = False):
        """Initialize WebSocket manager.
        
        Args:
            is_remote: True for remote server, False for local server
        """
        self.ws_client: Optional[websockets.WebSocketClientProtocol] = None
        self.server_url = REMOTE_SERVER_URL if is_remote else LOCAL_SERVER_URL
        self.message_callbacks: Dict[str, Callable] = {}
        self.connected = False
        
    async def connect(self) -> bool:
        """Connect to the WebSocket server.
        
        Returns:
            True if connection successful, False otherwise
        """
        try:
            self.ws_client = await websockets.connect(self.server_url)
            self.connected = True
            print(f"Connected to WebSocket server at {self.server_url}")
            
            # Register with the server
            if not await self._register():
                return False
                
            return True
        except Exception as e:
            print(f"WebSocket connection failed: {str(e)}")
            return False

    async def disconnect(self) -> None:
        """Disconnect from the WebSocket server."""
        if self.ws_client:
            await self.ws_client.close()
            self.connected = False
            print("Disconnected from WebSocket server")

    async def send_message(self, message_type: str, data: Dict[str, Any]) -> bool:
        """Send a message to the WebSocket server.
        
        Args:
            message_type: Type of message being sent
            data: Message data
            
        Returns:
            True if message sent successfully, False otherwise
        """
        if not self.connected:
            return False
            
        try:
            message = {
                "type": message_type,
                "data": data
            }
            await self.ws_client.send(json.dumps(message))
            return True
        except Exception as e:
            print(f"Failed to send message: {str(e)}")
            return False

    def register_callback(self, message_type: str, callback: Callable) -> None:
        """Register a callback for a specific message type.
        
        Args:
            message_type: Type of message to handle
            callback: Function to call when message is received
        """
        self.message_callbacks[message_type] = callback

    async def start_listening(self) -> None:
        """Start listening for WebSocket messages."""
        if not self.connected:
            return
            
        try:
            async for message in self.ws_client:
                try:
                    data = json.loads(message)
                    message_type = data.get("type")
                    message_data = data.get("data", {})
                    
                    if message_type in self.message_callbacks:
                        await self.message_callbacks[message_type](message_data)
                except json.JSONDecodeError:
                    print("Received invalid JSON message")
                except Exception as e:
                    print(f"Error processing message: {str(e)}")
        except websockets.exceptions.ConnectionClosed:
            print("WebSocket connection closed")
            self.connected = False
        except Exception as e:
            print(f"WebSocket error: {str(e)}")
            self.connected = False
            
    async def _register(self) -> bool:
        """Register with the WebSocket server.
        
        Returns:
            True if registration successful, False otherwise
        """
        try:
            await self.send_message(MSG_TYPE_REGISTER, {
                "pair_code": PAIR_CODE
            })
            
            response = await self.ws_client.recv()
            data = json.loads(response)
            
            if (data["type"] == MSG_TYPE_REGISTER_RESPONSE and 
                data.get("success", False)):
                print("Successfully registered with server")
                return True
            else:
                print("Server registration failed")
                return False
                
        except Exception as e:
            print(f"Registration failed: {str(e)}")
            return False 