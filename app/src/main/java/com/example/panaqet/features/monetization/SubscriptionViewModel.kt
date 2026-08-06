package com.example.panaqet.features.monetization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.Subscription
import com.example.panaqet.domain.repository.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            repository.getSubscriptions().collect { result ->
                if (result.isSuccess) {
                    val plans = result.getOrThrow()
                    repository.getMySubscription().collect { myResult ->
                        _uiState.value = SubscriptionUiState.Success(plans, myResult.getOrNull())
                    }
                } else {
                    _uiState.value = SubscriptionUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun subscribe(planId: Int) {
        viewModelScope.launch {
            repository.subscribe(planId, 30).collect { result ->
                if (result.isSuccess) {
                    loadPlans()
                }
            }
        }
    }
}

sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    data class Success(val plans: List<Subscription>, val current: Subscription?) : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
}
