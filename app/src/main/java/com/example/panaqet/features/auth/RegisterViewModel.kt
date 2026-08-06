package com.example.panaqet.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.User
import com.example.panaqet.domain.model.UserRole
import com.example.panaqet.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(
        username: String, email: String, password: String, role: UserRole,
        country: String, phoneNumber: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            repository.register(
                username, email, password, role,
                country, phoneNumber
            ).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    RegisterUiState.Success(result.getOrThrow())
                } else {
                    RegisterUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
