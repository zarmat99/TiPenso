import sys
import serial.tools.list_ports
import subprocess
import configparser
import os

def list_com_ports():
    """Lista tutte le porte COM disponibili"""
    ports = serial.tools.list_ports.comports()
    if not ports:
        print("Nessuna porta COM trovata!")
        return []
    
    print("\nPorte disponibili:")
    for port in ports:
        print(f"- {port.device}: {port.description}")
    return [port.device for port in ports]

def update_platformio_ini(com_port):
    """Aggiorna le porte in platformio.ini"""
    config = configparser.ConfigParser()
    config.read('platformio.ini')
    
    if 'env:esp32-c3-devkitm-1' in config:
        config['env:esp32-c3-devkitm-1']['upload_port'] = com_port
        config['env:esp32-c3-devkitm-1']['monitor_port'] = com_port
        
        with open('platformio.ini', 'w') as f:
            config.write(f)
        print(f"platformio.ini aggiornato con la porta {com_port}")
    else:
        print("Errore: sezione env:esp32-c3-devkitm-1 non trovata in platformio.ini")

def run_command(command):
    """Esegue un comando e gestisce gli errori"""
    try:
        subprocess.run(command, check=True, shell=True)
        return True
    except subprocess.CalledProcessError as e:
        print(f"Errore nell'esecuzione del comando: {e}")
        return False

def build():
    """Compila il progetto"""
    print("Compilazione in corso...")
    return run_command("pio run")

def upload(com_port):
    """Carica il firmware"""
    print(f"Caricamento del firmware su {com_port}...")
    return run_command(f"pio run --target upload --upload-port {com_port}")

def monitor(com_port):
    """Avvia il monitor seriale"""
    print(f"Avvio del monitor seriale su {com_port}...")
    return run_command(f"arduino-cli monitor --port {com_port} --config 115200")

def show_help():
    """Mostra l'help"""
    print("""
Utilizzo: python esp32.py [comando] [porta]

Comandi disponibili:
    build         - Solo compilazione
    upload        - Solo caricamento firmware
    monitor       - Solo monitor seriale
    all           - Esegue build, upload e monitor in sequenza
    list          - Lista le porte disponibili
    help          - Mostra questo help

Esempi:
    python esp32.py build              - Compila
    python esp32.py upload COM3        - Carica il firmware sulla COM3
    python esp32.py monitor COM5       - Avvia il monitor sulla COM5
    python esp32.py all COM7           - Esegue tutto sulla COM7
    python esp32.py list              - Mostra le porte disponibili
    """)

def main():
    if len(sys.argv) < 2 or sys.argv[1] in ['-h', '--help', 'help']:
        show_help()
        return

    command = sys.argv[1].lower()
    
    if command == 'list':
        list_com_ports()
        return

    # Usa COM15 come default se non specificata
    com_port = sys.argv[2] if len(sys.argv) > 2 else 'COM15'
    
    # Verifica se la porta esiste
    available_ports = list_com_ports()
    if com_port not in available_ports:
        print(f"Attenzione: {com_port} non trovata tra le porte disponibili!")
        response = input("Vuoi continuare comunque? (s/N): ")
        if response.lower() != 's':
            return

    # Aggiorna platformio.ini
    update_platformio_ini(com_port)

    if command == 'build':
        build()
    elif command == 'upload':
        upload(com_port)
    elif command == 'monitor':
        monitor(com_port)
    elif command == 'all':
        if build():
            if upload(com_port):
                monitor(com_port)
    else:
        print(f"Comando non riconosciuto: {command}")
        show_help()

if __name__ == "__main__":
    main() 
    