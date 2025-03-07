package com.tipenso.android.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleScanner(private val bluetoothAdapter: BluetoothAdapter) {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning
    
    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    // Utilizziamo filtri meno restrittivi o nessun filtro per trovare tutti i dispositivi
    // private val scanFilters = listOf<ScanFilter>() // Nessun filtro per trovare tutti i dispositivi
    
    // Oppure filtro solo per nome parziale
    private val scanFilters = listOf(
        ScanFilter.Builder()
            .setDeviceName("Ti Penso")
            .build()
    )

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // Log di tutti i dispositivi trovati per debug
            val deviceName = result.device.name ?: "Unknown"
            println("BLE Device found: $deviceName (${result.device.address})")
            
            val currentList = _scanResults.value.toMutableList()
            val existingDeviceIndex = currentList.indexOfFirst { 
                it.device.address == result.device.address 
            }
            
            if (existingDeviceIndex >= 0) {
                currentList[existingDeviceIndex] = result
            } else {
                currentList.add(result)
            }
            _scanResults.value = currentList
        }

        override fun onScanFailed(errorCode: Int) {
            println("BLE Scan failed with error code: $errorCode")
            when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> println("Scan already started")
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> println("Application registration failed")
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> println("Feature unsupported")
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> println("Internal error")
                else -> println("Unknown error")
            }
            _isScanning.value = false
            _scanResults.value = emptyList()
        }
    }

    fun startScan() {
        if (_isScanning.value) return
        
        println("Starting BLE scan with ${scanFilters.size} filters")
        _scanResults.value = emptyList()
        bluetoothAdapter.bluetoothLeScanner?.let { scanner ->
            _isScanning.value = true
            scanner.startScan(scanFilters, scanSettings, scanCallback)
        } ?: println("BluetoothLeScanner is null")
    }

    fun stopScan() {
        if (!_isScanning.value) return
        
        bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
        _isScanning.value = false
    }
} 