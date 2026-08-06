package com.example.panaqet.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.data.remote.PaymentApi
import com.example.panaqet.domain.repository.CartRepository
import com.example.panaqet.domain.repository.BuyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val buyerRepository: BuyerRepository,
    private val paymentApi: PaymentApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState

    fun preparePayment(email: String, amount: Double) {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading
            try {
                val response = paymentApi.initializePayment(mapOf(
                    "email" to email,
                    "amount" to amount.toString()
                ))
                _uiState.value = CheckoutUiState.PaymentReady(response.accessCode)
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error(e.message ?: "Failed to initialize payment")
            }
        }
    }

    fun onPaymentSuccess() {
        viewModelScope.launch {
            buyerRepository.placeOrder().collect { result ->
                if (result.isSuccess) {
                    cartRepository.clearCart()
                    _uiState.value = CheckoutUiState.Success
                } else {
                    _uiState.value = CheckoutUiState.Error("Payment succeeded but order placement failed on server.")
                }
            }
        }
    }
}

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    data class PaymentReady(val accessCode: String) : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}
