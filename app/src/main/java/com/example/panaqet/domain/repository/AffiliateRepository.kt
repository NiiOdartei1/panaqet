package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.AffiliateStats
import kotlinx.coroutines.flow.Flow

interface AffiliateRepository {
    fun getStats(): Flow<Result<AffiliateStats>>
    fun generateLink(productId: String, productName: String): Flow<Result<String>>
}
