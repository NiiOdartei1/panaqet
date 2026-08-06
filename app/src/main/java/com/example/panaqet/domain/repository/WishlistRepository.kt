package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(): Flow<Result<List<Product>>>
    fun addToWishlist(productId: String): Flow<Result<Unit>>
    fun removeFromWishlist(productId: String): Flow<Result<Unit>>
}
