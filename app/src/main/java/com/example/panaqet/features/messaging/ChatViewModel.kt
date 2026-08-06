package com.example.panaqet.features.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Message
import com.example.panaqet.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            repository.getMessages(conversationId).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    ChatUiState.Success(result.getOrThrow())
                } else {
                    ChatUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(conversationId, content).collect { result ->
                if (result.isSuccess) {
                    loadMessages(conversationId)
                }
            }
        }
    }
}

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}
