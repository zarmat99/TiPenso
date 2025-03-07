package com.tipenso.android.data.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Classe che gestisce la comunicazione con un dispositivo Ti Penso.
 * Implementa le stesse funzionalità del BLE Bridge in device_control.
 */
class TiPensoDevice(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _buttonState = MutableStateFlow(false)
    val buttonState: StateFlow<Boolean> = _buttonState
    
    // UUID delle caratteristiche (da device_control/core/constants.py)
    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("12345678-1234-5678-1234-56789abcdef0")
        val BUTTON_CHAR_UUID: UUID = UUID.fromString("f3d9e507-5fbe-48c6-9f07-0173f5fae9b0")
        val LED_CHAR_UUID: UUID = UUID.fromString("d0b39442-8be6-4d15-9610-054c3efc1c4f")
        const val DEVICE_NAME_PREFIX = "Ti Penso"
    }
    
    private val gattCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                handleConnectionStateChange(gatt, status, newState)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                handleServicesDiscovered(gatt, status)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                if (characteristic.uuid == BUTTON_CHAR_UUID) {
                    handleButtonStateChange(value)
                }
            }
        }
    } else {
        object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                handleConnectionStateChange(gatt, status, newState)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                handleServicesDiscovered(gatt, status)
            }

            @Suppress("DEPRECATION")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (characteristic.uuid == BUTTON_CHAR_UUID) {
                    characteristic.value?.let { handleButtonStateChange(it) }
                }
            }
        }
    }
    
    private fun handleConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        gatt?.let {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = ConnectionState.Connected
                    it.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    closeConnection()
                }
            }
        }
    }
    
    private fun handleServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
            // Cerca il servizio corretto
            val service = gatt.getService(SERVICE_UUID)
            if (service != null) {
                // Abilita le notifiche per la caratteristica del pulsante
                val buttonCharacteristic = service.getCharacteristic(BUTTON_CHAR_UUID)
                if (buttonCharacteristic != null) {
                    enableNotifications(gatt, buttonCharacteristic)
                }
            } else {
                _connectionState.value = ConnectionState.Error("Servizio non trovato")
            }
        }
    }
    
    private fun handleButtonStateChange(value: ByteArray) {
        // Converti il bytearray in stringa e prendi il primo carattere
        val stateChar = value.decodeToString().firstOrNull() ?: return
        val isPressed = stateChar == '1'
        _buttonState.value = isPressed
    }
    
    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
        _connectionState.value = ConnectionState.Connecting
    }
    
    fun disconnect() {
        closeConnection()
    }
    
    private fun closeConnection() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = ConnectionState.Disconnected
    }
    
    private fun enableNotifications(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)
    }
    
    fun setLedState(state: Boolean) {
        val stateValue = if (state) "1" else "0"
        val bytes = stateValue.toByteArray()
        
        val service = bluetoothGatt?.getService(SERVICE_UUID)
        val ledCharacteristic = service?.getCharacteristic(LED_CHAR_UUID)
        
        if (ledCharacteristic != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeCharacteristic(
                    ledCharacteristic,
                    bytes,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                @Suppress("DEPRECATION")
                ledCharacteristic.setValue(bytes)
                @Suppress("DEPRECATION")
                bluetoothGatt?.writeCharacteristic(ledCharacteristic)
            }
        }
    }
} 