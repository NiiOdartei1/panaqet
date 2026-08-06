package com.example.panaqet.features.affiliate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaqet.domain.model.AffiliateStats
import com.example.panaqet.domain.repository.AffiliateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffiliateViewModel @Inject constructor(
    private val repository: AffiliateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AffiliateUiState>(AffiliateUiState.Loading)
    val uiState: StateFlow<AffiliateUiState> = _uiState

    init {
        getStats()
    }

    fun getStats() {
        viewModelScope.launch {
            _uiState.value = AffiliateUiState.Loading
            repository.getStats().collect { result ->
                _uiState.value = if (result.isSuccess) {
                    AffiliateUiState.Success(result.getOrThrow())
                } else {
                    AffiliateUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun generateLink(productId: String, productName: String, onLinkGenerated: (String) -> Unit) {
        viewModelScope.launch {
            repository.generateLink(productId, productName).collect { result ->
                if (result.isSuccess) {
                    onLinkGenerated(result.getOrThrow())
                }
            }
        }
    }
}

sealed class AffiliateUiState {
    object Loading : AffiliateUiState()
    data class Success(val stats: AffiliateStats) : AffiliateUiState()
    data class Error(val message: String) : AffiliateUiState()
}
