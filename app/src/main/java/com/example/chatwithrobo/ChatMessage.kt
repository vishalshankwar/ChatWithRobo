package com.example.chatwithrobo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val helper = LlmModelHelper(app)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isModelReady = MutableStateFlow(false)
    val isModelReady: StateFlow<Boolean> = _isModelReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            helper.initialize()
            _isModelReady.value = true
        }
    }

    fun sendMessage(text: String) {
        if (!_isModelReady.value) return

        _messages.update { it + ChatMessage(text, isUser = true) }
        _messages.update { it + ChatMessage("", isUser = false) }

        _isLoading.value = true
        viewModelScope.launch {
            var full = ""
            helper.generateResponse(text).collect { chunk ->
                full += chunk
                _messages.update { list ->
                    list.toMutableList().also { it[it.lastIndex] = ChatMessage(full, isUser = false) }
                }
            }
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        helper.close()
        super.onCleared()
    }
}