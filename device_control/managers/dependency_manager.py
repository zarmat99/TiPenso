"""Dependency management module for the application."""

import subprocess
import sys
from typing import List

class DependencyManager:
    """Manages application dependencies."""
    
    REQUIRED_PACKAGES = [
        'bleak',
        'websockets',
        'aiohttp',
        'psutil'
    ]
    
    @staticmethod
    def check_dependencies() -> None:
        """Check and install required dependencies."""
        missing_packages = []
        
        for package in DependencyManager.REQUIRED_PACKAGES:
            try:
                __import__(package)
            except ImportError:
                missing_packages.append(package)
                
        if missing_packages:
            print("Installing dependencies...")
            DependencyManager.install_packages(missing_packages)
            
    @staticmethod
    def install_packages(packages: List[str]) -> None:
        """Install required packages.
        
        Args:
            packages: List of package names to install
        """
        try:
            subprocess.check_call([
                sys.executable,
                '-m',
                'pip',
                'install',
                *packages
            ])
            print("Dependencies installed successfully!")
        except subprocess.CalledProcessError as e:
            print(f"Error installing dependencies: {str(e)}")
            sys.exit(1) 