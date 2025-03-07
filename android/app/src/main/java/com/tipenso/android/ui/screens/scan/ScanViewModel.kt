package com.tipenso.android.ui.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tipenso.android.data.ble.BleScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.bluetooth.le.ScanResult

class ScanViewModel(
    private val bleScanner: BleScanner
) : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    val scanResults = bleScanner.scanResults

    fun startScan() {
        viewModelScope.launch {
            println("Starting BLE scan...")
            _isScanning.value = true
            bleScanner.startScan()
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            println("Stopping BLE scan...")
            _isScanning.value = false
            bleScanner.stopScan()
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
} 