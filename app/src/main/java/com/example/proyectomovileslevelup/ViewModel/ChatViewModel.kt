package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovileslevelup.Data.ChatRepository
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {

    val messages = ChatRepository.messages
    val isLoading = ChatRepository.isLoading

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            ChatRepository.sendMessage(text)
        }
    }

    fun clearChat() {
        ChatRepository.clearChat()
    }
}