package com.example.panaqet.features.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.CartItem
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.CartRepository
import com.example.panaqet.domain.repository.ProductRepository
import com.example.panaqet.domain.repository.BuyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val buyerRepository: BuyerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun getProduct(id: String) {
        viewModelScope.launch {
            productRepository.getProductById(id).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    ProductDetailUiState.Success(result.getOrThrow())
                } else {
                    ProductDetailUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(
                CartItem(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl ?: "",
                    quantity = 1
                )
            )
        }
    }

    fun addToWishlist(productId: String) {
        viewModelScope.launch {
            buyerRepository.addToWishlist(productId).collect { /* Handle success/failure */ }
        }
    }
}

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}
