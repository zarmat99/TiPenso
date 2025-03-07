package com.tipenso.android.di

import android.bluetooth.BluetoothManager
import android.content.Context
import com.tipenso.android.data.ble.BleManager
import com.tipenso.android.data.ble.BleScanner
import com.tipenso.android.ui.screens.connection.ConnectionViewModel
import com.tipenso.android.ui.screens.control.ControlViewModel
import com.tipenso.android.ui.screens.scan.ScanViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { 
        androidContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager 
    }
    single { 
        get<BluetoothManager>().adapter 
    }
    single { 
        BleManager(androidContext()) 
    }
    single { 
        BleScanner(get()) 
    }

    viewModel { 
        ScanViewModel(get()) 
    }
    viewModel { 
        ConnectionViewModel(get(), get()) 
    }
    viewModel { 
        ControlViewModel(get()) 
    }
} 