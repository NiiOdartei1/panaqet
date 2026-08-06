package com.example.panaqet.features.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Conversation
import com.example.panaqet.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationListUiState>(ConversationListUiState.Loading)
    val uiState: StateFlow<ConversationListUiState> = _uiState

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = ConversationListUiState.Loading
            repository.getConversations().collect { result ->
                _uiState.value = if (result.isSuccess) {
                    ConversationListUiState.Success(result.getOrThrow())
                } else {
                    ConversationListUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class ConversationListUiState {
    object Loading : ConversationListUiState()
    data class Success(val conversations: List<Conversation>) : ConversationListUiState()
    data class Error(val message: String) : ConversationListUiState()
}
