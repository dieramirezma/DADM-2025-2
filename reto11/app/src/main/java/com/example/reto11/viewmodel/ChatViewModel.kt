package com.example.reto11.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto11.data.ChatMessage
import com.example.reto11.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiKey: String?) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val geminiService: GeminiService? = apiKey?.let { GeminiService(it) }

    init {
        if (apiKey.isNullOrBlank()) {
            _error.value = "API Key no configurada"
        } else {
            // Mensaje de bienvenida
            addMessage("¡Hola! 👨‍🍳 Soy tu asistente de cocina especializado. Puedo ayudarte con recetas, técnicas culinarias, postres y cualquier pregunta relacionada con la cocina. ¿Qué te gustaría cocinar hoy?", false)
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank() || geminiService == null) {
            return
        }

        // Agregar mensaje del usuario
        addMessage(message, true)
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Construir el historial de conversación
                val conversationHistory = _messages.value.map { msg ->
                    if (msg.isUser) "Usuario: ${msg.text}" else "Asistente: ${msg.text}"
                }

                val result = geminiService.generateChatResponse(conversationHistory)

                result.onSuccess { response ->
                    addMessage(response, false)
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Error desconocido"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al procesar la solicitud"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val newMessage = ChatMessage(text = text, isUser = isUser)
        _messages.value = _messages.value + newMessage
    }

    fun clearError() {
        _error.value = null
    }
}

