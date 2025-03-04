# App Manager

L'App Manager è il componente principale che coordina l'avvio e l'arresto dell'intero sistema.

## Funzionalità

- Gestione del ciclo di vita dell'applicazione
- Coordinamento dei componenti
- Verifica delle dipendenze
- Gestione degli argomenti da riga di comando

## Struttura

```python
class AppManager:
    @staticmethod
    def start_local() -> None
    @staticmethod
    def start_remote() -> None
    @staticmethod
    def stop_local() -> None
    @staticmethod
    def stop_remote() -> None
```

## Utilizzo

### Da Riga di Comando

```batch
python app_manager.py [start|stop] [local|remote]
```

### Parametri

- `action`: Azione da eseguire ("start" o "stop")
- `version`: Versione del sistema ("local" o "remote")

### Esempi

1. Avvio versione locale:
   ```batch
   python app_manager.py start local
   ```

2. Avvio versione remota:
   ```batch
   python app_manager.py start remote
   ```

3. Arresto versione locale:
   ```batch
   python app_manager.py stop local
   ```

4. Arresto versione remota:
   ```batch
   python app_manager.py stop remote
   ```

## Flusso di Operazione

1. **Validazione Argomenti**
   - Verifica della presenza degli argomenti
   - Validazione dei valori degli argomenti
   - Impostazione dei valori di default

2. **Verifica Dipendenze**
   - Controllo delle dipendenze Python
   - Verifica della presenza dei file necessari
   - Controllo delle configurazioni

3. **Esecuzione Comando**
   - Avvio/arresto della versione locale
   - Avvio/arresto della versione remota
   - Gestione degli errori

## Gestione degli Errori

L'App Manager gestisce i seguenti scenari di errore:

1. **Argomenti Mancanti**
   - Mostra il messaggio di utilizzo
   - Termina con codice di errore

2. **Argomenti Non Validi**
   - Mostra il messaggio di errore
   - Termina con codice di errore

3. **Dipendenze Mancanti**
   - Mostra il messaggio di errore
   - Termina con codice di errore

4. **Errori di Esecuzione**
   - Log dell'errore
   - Termina con codice di errore

## Integrazione con Altri Componenti

L'App Manager interagisce con:

1. **Process Manager**
   - Avvio e arresto dei processi
   - Gestione delle finestre

2. **Dependency Manager**
   - Verifica delle dipendenze
   - Installazione delle dipendenze mancanti

## Note di Sviluppo

- Design modulare per facile estensione
- Gestione centralizzata degli errori
- Supporto per modalità locale e remota
- Interfaccia da riga di comando intuitiva

## Esempio di Output

```
Starting local version...
Starting server...
Starting Device 1...
Starting Device 2...
Ti Penso system started!

Stopping local version...
Stopping process 1234 (server.py)...
Stopping process 5678 (ble_bridge.py)...
Ti Penso system stopped! 