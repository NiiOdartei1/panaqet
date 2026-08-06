package com.example.panaqet.features.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketplaceUiState>(MarketplaceUiState.Loading)
    val uiState: StateFlow<MarketplaceUiState> = _uiState

    init {
        getProducts()
    }

    fun getProducts(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = MarketplaceUiState.Loading
            repository.getProducts(category).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    MarketplaceUiState.Success(result.getOrDefault(emptyList()))
                } else {
                    MarketplaceUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class MarketplaceUiState {
    object Loading : MarketplaceUiState()
    data class Success(val products: List<Product>) : MarketplaceUiState()
    data class Error(val message: String) : MarketplaceUiState()
}
