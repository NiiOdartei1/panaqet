package com.example.panaqet.features.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.StoreProfile
import com.example.panaqet.domain.repository.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreManagementViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StoreManagementUiState>(StoreManagementUiState.Loading)
    val uiState: StateFlow<StoreManagementUiState> = _uiState

    fun getProfile(sellerId: String) {
        viewModelScope.launch {
            _uiState.value = StoreManagementUiState.Loading
            repository.getStoreProfile(sellerId).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    StoreManagementUiState.Success(result.getOrThrow())
                } else {
                    StoreManagementUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun updateProfile(sellerId: String, profile: StoreProfile) {
        viewModelScope.launch {
            repository.updateStoreProfile(sellerId, profile).collect { /* Handle */ }
            getProfile(sellerId)
        }
    }
}

sealed class StoreManagementUiState {
    object Loading : StoreManagementUiState()
    data class Success(val profile: StoreProfile) : StoreManagementUiState()
    data class Error(val message: String) : StoreManagementUiState()
}
