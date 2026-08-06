package com.example.panaqet.features.buyer

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
class BuyerDashboardViewModel @Inject constructor(
    private val repository: BuyerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BuyerDashboardUiState>(BuyerDashboardUiState.Loading)
    val uiState: StateFlow<BuyerDashboardUiState> = _uiState

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            _uiState.value = BuyerDashboardUiState.Loading
            repository.getDashboardOrders().collect { result ->
                _uiState.value = if (result.isSuccess) {
                    BuyerDashboardUiState.Success(result.getOrDefault(emptyList()))
                } else {
                    BuyerDashboardUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class BuyerDashboardUiState {
    object Loading : BuyerDashboardUiState()
    data class Success(val orders: List<Order>) : BuyerDashboardUiState()
    data class Error(val message: String) : BuyerDashboardUiState()
}
