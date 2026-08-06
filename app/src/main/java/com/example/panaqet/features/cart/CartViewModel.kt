package com.example.panaqet.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.CartItem
import com.example.panaqet.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState(emptyList(), 0.0))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        repository.getCartItems().onEach { items ->
            val total = items.sumOf { it.price * it.quantity }
            _uiState.value = CartUiState(items, total)
        }.launchIn(viewModelScope)
    }

    fun updateQuantity(id: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity > 0) {
                repository.updateQuantity(id, quantity)
            } else {
                // Remove if quantity is 0? Or just leave it to user to delete.
                // For now, let's keep it simple.
                repository.updateQuantity(id, quantity)
            }
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}

data class CartUiState(
    val items: List<CartItem>,
    val totalPrice: Double
)
