# Ti Penso PlatformIO Project

Questo documento descrive la parte embedded del progetto Ti Penso, implementata su ESP32-C3 utilizzando PlatformIO.

## Struttura del Progetto

```
Embedded/
├── src/               # Codice sorgente
│   └── main.cpp      # File principale
├── include/          # Header files
├── lib/             # Librerie personalizzate
├── test/            # Test unitari
├── platformio.ini   # Configurazione PlatformIO
├── esp32.py         # Script di deployment
└── esp32.bat        # Batch file per il deployment
```

## Configurazione PlatformIO

Il progetto è configurato per ESP32-C3 con le seguenti impostazioni in `platformio.ini`:

```ini
[env:esp32-c3-devkitm-1]
platform = espressif32
board = esp32-c3-devkitm-1
framework = arduino
monitor_speed = 115200
upload_port = COM18
upload_protocol = esptool
board_build.f_flash = 40000000L
board_build.mcu = esp32c3
```

### Caratteristiche Principali

- **CPU**: ESP32-C3 (RISC-V)
- **Frequenza**: 80MHz
- **Flash**: 4MB
- **RAM**: 400KB
- **USB**: Supporto CDC
- **BLE**: Bluetooth Low Energy 5.0

## Funzionalità BLE

### Servizi e Caratteristiche

1. **Servizio Principale**
   - UUID: `12345678-1234-5678-1234-56789abcdef0`

2. **Caratteristiche**
   - **Pulsante** (Notify)
     - UUID: `f3d9e507-5fbe-48c6-9f07-0173f5fae9b0`
     - Invia lo stato del pulsante (0/1)
   
   - **LED** (Write)
     - UUID: `d0b39442-8be6-4d15-9610-054c3efc1c4f`
     - Riceve comandi per il LED (0/1)

### Comportamento

1. **Connessione**
   - Nome dispositivo: "Ti Penso"
   - Advertising automatico
   - Riconnessione automatica

2. **Pulsante**
   - Notifica quando premuto (1)
   - Notifica quando rilasciato (0)
   - Debounce integrato

3. **LED**
   - Acceso con comando "1"
   - Spento con comando "0"

## Pin Mapping

- **BUTTON_PIN**: 0 (GPIO0)
- **LED_PIN**: 2 (GPIO2)

## Ottimizzazioni

1. **Consumo Energetico**
   - CPU a 80MHz
   - Delay di 100ms nel loop principale
   - Consumo stimato: ~87mA

2. **Performance**
   - BLE ottimizzato per bassa latenza
   - Gestione efficiente delle notifiche
   - Debounce hardware del pulsante

## Deployment

### Requisiti

- PlatformIO installato
- ESP32-C3 collegato via USB
- Porta COM corretta configurata

### Procedura

1. **Compilazione**
   ```bash
   pio run
   ```

2. **Upload**
   ```bash
   pio run --target upload
   ```

3. **Monitor**
   ```bash
   pio device monitor
   ```

### Script di Deployment

Il progetto include uno script Python (`esp32.py`) per automatizzare il deployment:

```bash
python esp32.py all COMx  # Sostituire COMx con la porta corretta
```

## Debug

### Output Serial

- Velocità: 115200 baud
- Filtri: esp32_exception_decoder, default
- Debug abilitato (DEBUG = 1)

### Messaggi di Debug

1. **Inizializzazione**
   - "Start"
   - "BLE ready, waiting for connections..."

2. **Connessione**
   - "Device connected!"
   - "Initial state -> value 0 sent!"

3. **Pulsante**
   - "Button pressed -> value 1 sent!"
   - "Button released -> value 0 sent!"

4. **LED**
   - "Command: Turn on LED!"
   - "Command: Turn off LED!"

## Troubleshooting

1. **Dispositivo non rilevato**
   - Verificare la connessione USB
   - Controllare la porta COM nel Device Manager
   - Provare un cavo USB diverso

2. **Upload fallito**
   - Mettere la board in modalità download
   - Verificare la porta COM corretta
   - Controllare i driver USB

3. **BLE non funzionante**
   - Verificare l'alimentazione
   - Controllare le connessioni
   - Riavviare il dispositivo

## Note di Sviluppo

- Utilizza il framework Arduino per ESP32
- Supporta il debug via USB CDC
- Implementa un sistema di notifiche BLE efficiente
- Gestisce gli errori in modo robusto 