package com.example.panaqet.features.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    val uiState: StateFlow<AddProductUiState> = _uiState

    fun addProduct(
        name: String,
        description: String,
        price: String,
        category: String,
        stock: String,
        imageFile: File?,
        condition: String = "New",
        location: String? = null,
        isPackage: Boolean = false
    ) {
        if (imageFile == null) {
            _uiState.value = AddProductUiState.Error("Please select an image")
            return
        }

        val priceDouble = price.toDoubleOrNull()
        val stockInt = stock.toIntOrNull()

        if (priceDouble == null || stockInt == null) {
            _uiState.value = AddProductUiState.Error("Invalid price or stock")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddProductUiState.Loading
            repository.addProduct(
                name, description, priceDouble, category, stockInt, imageFile,
                condition, location, isPackage
            ).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    AddProductUiState.Success
                } else {
                    AddProductUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed class AddProductUiState {
    object Idle : AddProductUiState()
    object Loading : AddProductUiState()
    object Success : AddProductUiState()
    data class Error(val message: String) : AddProductUiState()
}
