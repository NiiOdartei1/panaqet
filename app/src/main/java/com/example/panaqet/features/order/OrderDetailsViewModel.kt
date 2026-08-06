package com.example.panaqet.features.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.repository.BuyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val repository: BuyerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Loading)
    val uiState: StateFlow<OrderDetailsUiState> = _uiState

    fun getOrder(id: String) {
        viewModelScope.launch {
            _uiState.value = OrderDetailsUiState.Loading
            repository.getOrderById(id).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    OrderDetailsUiState.Success(result.getOrThrow())
                } else {
                    OrderDetailsUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class OrderDetailsUiState {
    object Loading : OrderDetailsUiState()
    data class Success(val order: Order) : OrderDetailsUiState()
    data class Error(val message: String) : OrderDetailsUiState()
}
