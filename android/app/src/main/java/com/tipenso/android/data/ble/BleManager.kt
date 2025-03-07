package com.tipenso.android.data.ble

import android.bluetooth.*
import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class BleManager(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    // UUID del servizio e delle caratteristiche (da device_control/ble_bridge.py)
    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("12345678-1234-5678-1234-56789abcdef0")
        val BUTTON_CHARACTERISTIC_UUID: UUID = UUID.fromString("f3d9e507-5fbe-48c6-9f07-0173f5fae9b0")
        val LED_CHARACTERISTIC_UUID: UUID = UUID.fromString("d0b39442-8be6-4d15-9610-054c3efc1c4f")
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
                handleCharacteristicValue(value)
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
                characteristic.value?.let { handleCharacteristicValue(it) }
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
            val buttonCharacteristic = getButtonCharacteristic(gatt)
            if (buttonCharacteristic != null) {
                enableNotifications(gatt, buttonCharacteristic)
            }
        }
    }

    private fun handleCharacteristicValue(value: ByteArray) {
        val message = String(value)
        // Gestione del messaggio ricevuto
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

    private fun getButtonCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
        val service = gatt.getService(SERVICE_UUID)
        return service?.getCharacteristic(BUTTON_CHARACTERISTIC_UUID)
    }

    private fun getLedCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
        val service = gatt.getService(SERVICE_UUID)
        return service?.getCharacteristic(LED_CHARACTERISTIC_UUID)
    }

    private fun enableNotifications(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)
    }

    fun sendMessage(message: String) {
        val characteristic = bluetoothGatt?.let { getLedCharacteristic(it) }
        characteristic?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeCharacteristic(
                    it,
                    message.toByteArray(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                @Suppress("DEPRECATION")
                it.setValue(message.toByteArray())
                @Suppress("DEPRECATION")
                bluetoothGatt?.writeCharacteristic(it)
            }
        }
    }

    private fun handleGattError(status: Int) {
        when (status) {
            BluetoothGatt.GATT_FAILURE -> _connectionState.value = 
                ConnectionState.Error("GATT operation failed")
            BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> _connectionState.value = 
                ConnectionState.Error("GATT insufficient authentication")
            BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> _connectionState.value = 
                ConnectionState.Error("GATT invalid attribute length")
            else -> _connectionState.value = 
                ConnectionState.Error("Unknown GATT error: $status")
        }
    }
} 