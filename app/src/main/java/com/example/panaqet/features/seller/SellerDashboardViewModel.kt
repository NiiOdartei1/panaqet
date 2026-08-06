package com.example.panaqet.features.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.model.SellerStats
import com.example.panaqet.domain.repository.ProductRepository
import com.example.panaqet.domain.repository.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerDashboardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerDashboardUiState>(SellerDashboardUiState.Loading)
    val uiState: StateFlow<SellerDashboardUiState> = _uiState

    fun getSellerProducts(sellerId: String) {
        viewModelScope.launch {
            _uiState.value = SellerDashboardUiState.Loading
            productRepository.getProducts(sellerId = sellerId).collect { result ->
                if (result.isSuccess) {
                    val products = result.getOrDefault(emptyList())
                    sellerRepository.getSellerStats(sellerId).collect { statsResult ->
                        _uiState.value = if (statsResult.isSuccess) {
                            SellerDashboardUiState.Success(products, statsResult.getOrNull())
                        } else {
                            SellerDashboardUiState.Success(products, null)
                        }
                    }
                } else {
                    _uiState.value = SellerDashboardUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun toggleAvailability(productId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            productRepository.updateProductAvailability(productId, isAvailable).collect { result ->
                if (result.isSuccess) {
                    getSellerProducts("s1")
                }
            }
        }
    }
}

sealed class SellerDashboardUiState {
    object Loading : SellerDashboardUiState()
    data class Success(
        val products: List<Product>,
        val stats: SellerStats? = null
    ) : SellerDashboardUiState()
    data class Error(val message: String) : SellerDashboardUiState()
}
