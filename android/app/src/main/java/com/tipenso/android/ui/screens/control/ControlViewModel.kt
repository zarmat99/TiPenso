package com.tipenso.android.ui.screens.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tipenso.android.data.ble.BleManager
import com.tipenso.android.data.ble.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ControlViewModel(
    private val bleManager: BleManager
) : ViewModel() {
    val connectionState: StateFlow<ConnectionState> = bleManager.connectionState
    
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    fun sendCommand(command: String) {
        viewModelScope.launch {
            try {
                bleManager.sendMessage(command)
                addMessage("Sent: $command")
            } catch (e: Exception) {
                addMessage("Error sending command: ${e.message}")
            }
        }
    }

    private fun addMessage(message: String) {
        _messages.value = _messages.value + message
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.disconnect()
    }
} 