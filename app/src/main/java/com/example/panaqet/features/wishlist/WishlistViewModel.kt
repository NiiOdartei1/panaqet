package com.example.panaqet.features.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.BuyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repository: BuyerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WishlistUiState>(WishlistUiState.Loading)
    val uiState: StateFlow<WishlistUiState> = _uiState

    init {
        getWishlist()
    }

    fun getWishlist() {
        viewModelScope.launch {
            _uiState.value = WishlistUiState.Loading
            repository.getWishlist().collect { result ->
                _uiState.value = if (result.isSuccess) {
                    WishlistUiState.Success(result.getOrDefault(emptyList()))
                } else {
                    WishlistUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            repository.removeFromWishlist(productId).collect { result ->
                if (result.isSuccess) {
                    getWishlist()
                }
            }
        }
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            repository.addToRemoteCart(productId, 1).collect { /* Handle */ }
        }
    }
}

sealed class WishlistUiState {
    object Loading : WishlistUiState()
    data class Success(val products: List<Product>) : WishlistUiState()
    data class Error(val message: String) : WishlistUiState()
}
