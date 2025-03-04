# Ti Penso Device Control System

Sistema di controllo per i dispositivi Ti Penso che gestisce la comunicazione tra i dispositivi BLE e il server WebSocket.

## Struttura del Progetto

```
device_control/
├── core/               # Costanti e configurazioni condivise
├── managers/          # Gestori dei processi e delle dipendenze
├── protocols/         # Protocolli di comunicazione
├── scripts/          # Script eseguibili
│   ├── server.py     # Server WebSocket
│   └── ble_bridge.py # Bridge BLE per i dispositivi
└── run.bat           # Script batch per avviare/fermare il sistema
```

## Requisiti

- Python 3.8 o superiore
- Windows 10 o superiore
- Dipendenze Python (installate automaticamente):
  - bleak (per la comunicazione BLE)
  - websockets (per la comunicazione WebSocket)
  - psutil (per la gestione dei processi)

## Installazione

1. Clona il repository
2. Naviga nella directory `device_control`
3. Esegui il comando per installare le dipendenze:
   ```batch
   pip install -r requirements.txt
   ```

## Utilizzo

Il sistema può essere avviato in due modalità: locale e remota.

### Avvio del Sistema

```batch
run.bat start [local|remote]
```

- `local`: Avvia il server locale e i bridge BLE
- `remote`: Avvia solo i bridge BLE per la connessione a un server remoto

### Arresto del Sistema

```batch
run.bat stop [local|remote]
```

### Esempi

1. Avvio versione locale:
   ```batch
   run.bat start local
   ```

2. Avvio versione remota:
   ```batch
   run.bat start remote
   ```

3. Arresto versione locale:
   ```batch
   run.bat stop local
   ```

4. Arresto versione remota:
   ```batch
   run.bat stop remote
   ```

## Componenti Principali

### App Manager (`app_manager.py`)
- Gestisce il ciclo di vita dell'applicazione
- Coordina l'avvio e l'arresto dei componenti
- Verifica le dipendenze necessarie

### Process Manager (`managers/process_manager.py`)
- Gestisce i processi Python
- Avvia e ferma i componenti del sistema
- Mantiene le finestre del comando aperte per il monitoraggio

### BLE Bridge (`scripts/ble_bridge.py`)
- Gestisce la comunicazione con i dispositivi BLE
- Supporta sia la modalità locale che remota
- Gestisce la connessione WebSocket con il server

### Server (`scripts/server.py`)
- Gestisce le connessioni WebSocket
- Coordina la comunicazione tra i dispositivi
- Gestisce la registrazione dei dispositivi

## Configurazione

Le costanti e le configurazioni sono definite in `core/constants.py`:

- `LOCAL_SERVER_URL`: URL del server locale
- `REMOTE_SERVER_URL`: URL del server remoto
- `PAIR_CODE`: Codice di accoppiamento per la registrazione
- `DEVICE_NAME_PREFIX`: Prefisso per il nome dei dispositivi
- `BUTTON_CHAR_UUID`: UUID della caratteristica del pulsante
- `LED_CHAR_UUID`: UUID della caratteristica del LED

## Troubleshooting

1. **Finestre che si chiudono immediatamente**
   - Verifica che Python sia installato correttamente
   - Controlla che tutte le dipendenze siano installate

2. **Connessione BLE fallita**
   - Verifica che i dispositivi siano accesi e in range
   - Controlla che il Bluetooth sia attivo sul computer

3. **Connessione server fallita**
   - Verifica che l'URL del server sia corretto
   - Controlla che il server sia in esecuzione
   - Verifica che il firewall non blocchi le connessioni

## Note di Sviluppo

- Il sistema è progettato per essere modulare e facilmente estensibile
- I componenti comunicano attraverso WebSocket per massima flessibilità
- La gestione dei processi è centralizzata per una migliore manutenibilità 