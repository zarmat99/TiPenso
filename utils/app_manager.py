import subprocess
import sys
import time
import os
import signal
import psutil

def find_python_processes(script_name):
    """Trova i processi Python che eseguono uno specifico script."""
    processes = []
    for proc in psutil.process_iter(['pid', 'name', 'cmdline']):
        try:
            if proc.info['name'] == 'python.exe' and script_name in ' '.join(proc.info['cmdline']):
                processes.append(proc)
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            pass
    return processes

def stop_processes(script_name):
    """Arresta i processi Python che eseguono uno specifico script."""
    processes = find_python_processes(script_name)
    for proc in processes:
        try:
            print(f"Arresto processo {proc.info['pid']} ({script_name})...")
            proc.terminate()
            proc.wait(timeout=3)
        except psutil.TimeoutExpired:
            proc.kill()
        except psutil.NoSuchProcess:
            pass

def start_remote():
    """Avvia la versione remota dell'applicazione."""
    print("Avvio versione remota...")
    
    # Avvia il primo bridge remoto
    print("Avvio Device 1 Remoto...")
    subprocess.Popen(['python', 'ble_bridge_remote.py', '1'], 
                     creationflags=subprocess.CREATE_NEW_CONSOLE)
    
    time.sleep(2)  # Attende 2 secondi tra i bridge
    
    # Avvia il secondo bridge remoto
    print("Avvio Device 2 Remoto...")
    subprocess.Popen(['python', 'ble_bridge_remote.py', '2'], 
                     creationflags=subprocess.CREATE_NEW_CONSOLE)

def start_local():
    """Avvia la versione locale dell'applicazione."""
    print("Avvio versione locale...")
    
    # Avvia il server
    print("Avvio server...")
    subprocess.Popen(['python', 'server.py'], 
                     creationflags=subprocess.CREATE_NEW_CONSOLE)
    
    time.sleep(3)  # Attende 3 secondi per assicurarsi che il server sia attivo
    
    # Avvia il primo bridge
    print("Avvio Device 1...")
    subprocess.Popen(['python', 'ble_bridge.py', '1'], 
                     creationflags=subprocess.CREATE_NEW_CONSOLE)
    
    time.sleep(2)  # Attende 2 secondi tra i bridge
    
    # Avvia il secondo bridge
    print("Avvio Device 2...")
    subprocess.Popen(['python', 'ble_bridge.py', '2'], 
                     creationflags=subprocess.CREATE_NEW_CONSOLE)

def stop_remote():
    """Arresta la versione remota dell'applicazione."""
    print("Arresto versione remota...")
    stop_processes('ble_bridge_remote.py')

def stop_local():
    """Arresta la versione locale dell'applicazione."""
    print("Arresto versione locale...")
    stop_processes('server.py')
    stop_processes('ble_bridge.py')

def check_dependencies():
    """Verifica e installa le dipendenze necessarie."""
    try:
        import bleak
    except ImportError:
        print("Installazione dipendenze...")
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'bleak', 'websockets', 'aiohttp', 'psutil'])

def main():
    if len(sys.argv) < 2:
        print("Uso: python app_manager.py [start|stop] [local|remote]")
        print("Esempio: python app_manager.py start local")
        print("         python app_manager.py stop remote")
        return

    action = sys.argv[1].lower()
    version = sys.argv[2].lower() if len(sys.argv) > 2 else 'local'

    if action not in ['start', 'stop']:
        print("Azione non valida. Usa 'start' o 'stop'")
        return

    if version not in ['local', 'remote']:
        print("Versione non valida. Usa 'local' o 'remote'")
        return

    check_dependencies()

    if action == 'start':
        if version == 'local':
            start_local()
        else:
            start_remote()
        print("Sistema Ti Penso avviato!")
    else:
        if version == 'local':
            stop_local()
        else:
            stop_remote()
        print("Sistema Ti Penso arrestato!")

if __name__ == "__main__":
    main() 