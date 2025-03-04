# Process Manager

Il Process Manager è il componente che gestisce il ciclo di vita dei processi Python dell'applicazione.

## Funzionalità

- Avvio e arresto dei processi Python
- Gestione delle finestre del comando
- Monitoraggio dello stato dei processi
- Gestione dei timeout e degli errori
- Pulizia automatica dei processi esistenti

## Configurazione

Il manager utilizza le seguenti costanti definite in `core/constants.py`:

```python
# Process Management
PYTHON_EXECUTABLE = "python.exe"
PROCESS_TIMEOUT = 3  # seconds
BRIDGE_START_DELAY = 2  # seconds between bridge starts
SERVER_START_DELAY = 3  # seconds to wait for server startup

# Script Names and Paths
SCRIPT_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "scripts")
SERVER_SCRIPT = os.path.join(SCRIPT_DIR, "server.py")
BLE_BRIDGE_SCRIPT = os.path.join(SCRIPT_DIR, "ble_bridge.py")
```

## Metodi Principali

### `find_python_processes(script_name: str) -> List[psutil.Process]`
Trova tutti i processi Python che eseguono uno specifico script.

```python
processes = ProcessManager.find_python_processes("server.py")
```

### `stop_processes(script_name: str) -> None`
Ferma tutti i processi Python che eseguono uno specifico script.

```python
ProcessManager.stop_processes("server.py")
```

### `is_process_running(script_name: str) -> bool`
Verifica se uno specifico script è in esecuzione.

```python
if ProcessManager.is_process_running("server.py"):
    print("Server is running")
```

### `start_process(script: str, args: Optional[List[str]] = None) -> None`
Avvia un processo Python con argomenti opzionali in una nuova finestra del comando.

```python
ProcessManager.start_process("server.py")
ProcessManager.start_process("ble_bridge.py", ["1", "local"])
```

## Modalità di Operazione

### Versione Locale

1. **Avvio**
   ```python
   ProcessManager.start_local()
   ```
   - Ferma eventuali processi esistenti
   - Attende che i processi siano completamente fermati
   - Avvia il server
   - Attende il delay di avvio del server
   - Avvia i bridge BLE in sequenza
   - Attende il delay tra i bridge

2. **Arresto**
   ```python
   ProcessManager.stop_local()
   ```
   - Ferma il server
   - Ferma i bridge BLE

### Versione Remota

1. **Avvio**
   ```python
   ProcessManager.start_remote()
   ```
   - Avvia i bridge BLE in sequenza
   - Attende il delay tra i bridge

2. **Arresto**
   ```python
   ProcessManager.stop_remote()
   ```
   - Ferma i bridge BLE

## Gestione delle Finestre

Il manager gestisce le finestre del comando in modo che:
- Ogni processo abbia la propria finestra
- Le finestre rimangano aperte per il monitoraggio
- L'output sia visibile in tempo reale
- Le finestre siano create in modo nativo Windows

## Gestione degli Errori

Il manager gestisce i seguenti scenari di errore:

1. **Processo non trovato**
   - Log dell'errore
   - Continuazione dell'esecuzione

2. **Timeout durante l'arresto**
   - Tentativo di terminazione forzata
   - Log dell'errore

3. **Accesso negato**
   - Log dell'errore
   - Continuazione dell'esecuzione

4. **Porta già in uso**
   - Ferma i processi esistenti
   - Attende che le porte siano liberate
   - Riavvia i processi

## Note di Sviluppo

- Utilizza `psutil` per la gestione dei processi
- Implementa un sistema di timeout per l'arresto dei processi
- Gestisce le finestre del comando in modo nativo Windows
- Supporta l'avvio e l'arresto ordinato dei componenti
- Pulisce automaticamente i processi esistenti prima dell'avvio 