package com.tipenso.android.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tipenso.android.TiPensoApplication
import com.tipenso.android.data.ble.BleManager
import com.tipenso.android.ui.screens.control.ControlViewModel
import com.tipenso.android.ui.screens.scan.ScanViewModel
import com.tipenso.android.ui.screens.connection.ConnectionViewModel

class ViewModelFactory(
    private val application: TiPensoApplication,
    private val bleManager: BleManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(application.bleScanner) as T
            }
            modelClass.isAssignableFrom(ConnectionViewModel::class.java) -> {
                ConnectionViewModel(bleManager, application.bluetoothAdapter) as T
            }
            modelClass.isAssignableFrom(ControlViewModel::class.java) -> {
                ControlViewModel(bleManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 