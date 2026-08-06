package com.example.panaqet.domain.repository

import com.example.panaqet.data.remote.dto.CartItemDto
import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface BuyerRepository {
    fun getWishlist(): Flow<Result<List<Product>>>
    fun addToWishlist(productId: String): Flow<Result<Unit>>
    fun removeFromWishlist(productId: String): Flow<Result<Unit>>
    fun getDashboardOrders(): Flow<Result<List<Order>>>
    fun getRemoteCart(): Flow<Result<List<CartItemDto>>>
    fun addToRemoteCart(productId: String, quantity: Int): Flow<Result<Unit>>
    fun removeFromRemoteCart(cartId: Int): Flow<Result<Unit>>
    fun clearRemoteCart(): Flow<Result<Unit>>
    fun placeOrder(): Flow<Result<Order>>
    fun getOrderById(orderId: String): Flow<Result<Order>>
}
