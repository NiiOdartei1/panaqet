package com.example.panaqet.features.monetization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.CommissionPlan
import com.example.panaqet.domain.repository.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommissionPlanViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommissionUiState>(CommissionUiState.Loading)
    val uiState: StateFlow<CommissionUiState> = _uiState

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = CommissionUiState.Loading
            repository.getCommissionPlans().collect { result ->
                _uiState.value = if (result.isSuccess) {
                    CommissionUiState.Success(result.getOrThrow())
                } else {
                    CommissionUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun createPlan(name: String, rate: Double, description: String?) {
        viewModelScope.launch {
            repository.createCommissionPlan(name, rate, description).collect { result ->
                if (result.isSuccess) {
                    loadPlans()
                }
            }
        }
    }
}

sealed class CommissionUiState {
    object Loading : CommissionUiState()
    data class Success(val plans: List<CommissionPlan>) : CommissionUiState()
    data class Error(val message: String) : CommissionUiState()
}
