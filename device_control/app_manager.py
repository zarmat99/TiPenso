"""Main application manager for device control system."""

import sys
from typing import Optional
from device_control.managers.process_manager import ProcessManager
from device_control.managers.dependency_manager import DependencyManager

class AppManager:
    """Main application manager coordinating all components."""
    
    @staticmethod
    def start_local() -> None:
        """Start the local version of the application."""
        print("Starting local version...")
        ProcessManager.start_local()
        print("Ti Penso system started!")
        
    @staticmethod
    def start_remote() -> None:
        """Start the remote version of the application."""
        print("Starting remote version...")
        ProcessManager.start_remote()
        print("Ti Penso system started!")
        
    @staticmethod
    def stop_local() -> None:
        """Stop the local version of the application."""
        print("Stopping local version...")
        ProcessManager.stop_local()
        print("Ti Penso system stopped!")
        
    @staticmethod
    def stop_remote() -> None:
        """Stop the remote version of the application."""
        print("Stopping remote version...")
        ProcessManager.stop_remote()
        print("Ti Penso system stopped!")

def main():
    """Main entry point for the application."""
    if len(sys.argv) < 2:
        print("Usage: python app_manager.py [start|stop] [local|remote]")
        print("Example: python app_manager.py start local")
        print("         python app_manager.py stop remote")
        return

    action = sys.argv[1].lower()
    version = sys.argv[2].lower() if len(sys.argv) > 2 else 'local'

    if action not in ['start', 'stop']:
        print("Invalid action. Use 'start' or 'stop'")
        return

    if version not in ['local', 'remote']:
        print("Invalid version. Use 'local' or 'remote'")
        return

    # Check and install dependencies
    DependencyManager.check_dependencies()

    # Execute requested action
    if action == 'start':
        if version == 'local':
            AppManager.start_local()
        else:
            AppManager.start_remote()
    else:
        if version == 'local':
            AppManager.stop_local()
        else:
            AppManager.stop_remote()

if __name__ == "__main__":
    main() 