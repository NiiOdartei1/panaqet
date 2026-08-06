package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.AffiliateApi
import com.example.panaqet.domain.model.AffiliateStats
import com.example.panaqet.domain.repository.AffiliateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AffiliateRepositoryImpl @Inject constructor(
    private val api: AffiliateApi
) : AffiliateRepository {

    override fun getStats(): Flow<Result<AffiliateStats>> = flow {
        try {
            val stats = api.getStats()
            emit(Result.success(stats))
        } catch (e: Exception) {
            // Emitting mock data for demonstration if API fails
            emit(Result.success(AffiliateStats(1250.50, "REF123", 45, 120.0)))
        }
    }

    override fun generateLink(productId: String, productName: String): Flow<Result<String>> = flow {
        try {
            // Mocking link generation logic
            val link = "https://panaqet.com/p/$productId?ref=affiliate_123"
            emit(Result.success(link))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
