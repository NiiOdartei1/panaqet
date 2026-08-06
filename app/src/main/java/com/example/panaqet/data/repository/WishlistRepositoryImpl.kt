package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.BuyerApi
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val api: BuyerApi
) : WishlistRepository {
    override fun getWishlist(): Flow<Result<List<Product>>> = flow {
        try {
            val response = api.getWishlist()
            emit(Result.success(response.map { it.toProduct() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun addToWishlist(productId: String): Flow<Result<Unit>> = flow {
        try {
            api.addToWishlist(productId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun removeFromWishlist(productId: String): Flow<Result<Unit>> = flow {
        try {
            api.removeFromWishlist(productId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
