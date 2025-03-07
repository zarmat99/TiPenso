package com.tipenso.android

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.tipenso.android.data.ble.BleManager
import com.tipenso.android.data.ble.BleScanner
import com.tipenso.android.data.ble.TiPensoDevice
import com.tipenso.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TiPensoApplication : Application() {
    // Inizializzazione lazy per evitare problemi all'avvio
    val bleManager by lazy { BleManager(applicationContext) }
    val tiPensoDevice by lazy { TiPensoDevice(applicationContext) }
    
    // Queste proprietà saranno inizializzate quando necessario
    lateinit var bleScanner: BleScanner
        private set
    
    lateinit var bluetoothAdapter: BluetoothAdapter
        private set

    override fun onCreate() {
        super.onCreate()
        // Non inizializzare qui il Bluetooth per evitare crash
    }

    fun initializeBluetooth(adapter: BluetoothAdapter) {
        bluetoothAdapter = adapter
        bleScanner = BleScanner(adapter)
    }
} 