package com.example.panaqet.features.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.OrderStatus
import com.example.panaqet.domain.repository.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderManagementViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderManagementUiState>(OrderManagementUiState.Loading)
    val uiState: StateFlow<OrderManagementUiState> = _uiState

    fun getOrders(sellerId: String) {
        viewModelScope.launch {
            _uiState.value = OrderManagementUiState.Loading
            repository.getOrdersBySeller(sellerId).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    OrderManagementUiState.Success(result.getOrDefault(emptyList()))
                } else {
                    OrderManagementUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun updateStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status).collect { /* Handle response */ }
            // Refresh list
            getOrders("s1")
        }
    }

    fun updateDeliveryStatus(orderId: String, deliveryStatus: String) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(orderId, deliveryStatus).collect { /* Handle response */ }
            getOrders("s1")
        }
    }
}

sealed class OrderManagementUiState {
    object Loading : OrderManagementUiState()
    data class Success(val orders: List<Order>) : OrderManagementUiState()
    data class Error(val message: String) : OrderManagementUiState()
}
