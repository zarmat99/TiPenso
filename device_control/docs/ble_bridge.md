# BLE Bridge

Il BLE Bridge è il componente che gestisce la comunicazione tra i dispositivi Ti Penso e il server WebSocket.

## Funzionalità

- Scansione e connessione ai dispositivi BLE
- Gestione delle notifiche del pulsante
- Comunicazione bidirezionale con il server WebSocket
- Supporto per modalità locale e remota

## Configurazione

Il bridge utilizza le seguenti costanti definite in `core/constants.py`:

```python
# BLE Constants
BUTTON_CHAR_UUID = "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0"
LED_CHAR_UUID = "d0b39442-8be6-4d15-9610-054c3efc1c4f"
DEVICE_NAME_PREFIX = "Ti Penso"

# Server Configuration
LOCAL_SERVER_URL = "ws://localhost:8080/ws"
REMOTE_SERVER_URL = "ws://your-remote-server:8080/ws"
PAIR_CODE = "12345"
```

## Utilizzo

Il bridge può essere avviato in due modalità:

1. **Locale**:
   ```batch
   python ble_bridge.py 1 local
   python ble_bridge.py 2 local
   ```

2. **Remota**:
   ```batch
   python ble_bridge.py 1 remote
   python ble_bridge.py 2 remote
   ```

### Parametri

- `device_number`: Numero del dispositivo (1 o 2)
- `version`: Modalità di operazione ("local" o "remote")

## Flusso di Operazione

1. **Inizializzazione**
   - Creazione dell'istanza BLEBridge
   - Configurazione dell'URL del server in base alla modalità

2. **Connessione BLE**
   - Scansione dei dispositivi disponibili
   - Identificazione del dispositivo Ti Penso
   - Connessione e configurazione delle notifiche

3. **Connessione Server**
   - Connessione WebSocket al server
   - Registrazione del dispositivo con il codice di accoppiamento
   - Verifica della risposta di registrazione

4. **Loop Principale**
   - Gestione delle notifiche del pulsante
   - Invio degli stati al server
   - Ricezione dei comandi per il LED
   - Gestione degli errori e riconnessione

## Gestione degli Errori

Il bridge gestisce i seguenti scenari di errore:

1. **Dispositivo non trovato**
   - Messaggio di errore e terminazione
   - Possibilità di riavvio manuale

2. **Connessione BLE fallita**
   - Tentativo di riconnessione
   - Timeout e terminazione in caso di fallimento

3. **Connessione server fallita**
   - Disconnessione BLE
   - Terminazione del processo

4. **Perdita di connessione**
   - Chiusura pulita delle connessioni
   - Terminazione del processo

## Messaggi

### Inviati al Server

1. **Registrazione**
   ```json
   {
     "type": "register",
     "pair_code": "12345"
   }
   ```

2. **Stato Pulsante**
   ```json
   {
     "type": "button_state",
     "state": "1"  // o "0"
   }
   ```

### Ricevuti dal Server

1. **Risposta Registrazione**
   ```json
   {
     "type": "register_response",
     "success": true
   }
   ```

2. **Comando LED**
   ```json
   {
     "type": "button_state",
     "state": "1"  // o "0"
   }
   ```

3. **Errore**
   ```json
   {
     "type": "error",
     "message": "messaggio di errore"
   }
   ```

## Note di Sviluppo

- Utilizza la libreria `bleak` per la comunicazione BLE
- Implementa un sistema di riconnessione automatica
- Gestisce le connessioni in modo asincrono con `asyncio`
- Supporta la comunicazione bidirezionale in tempo reale 