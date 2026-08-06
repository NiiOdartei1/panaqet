package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.BuyerApi
import com.example.panaqet.data.remote.dto.CartItemDto
import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.BuyerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BuyerRepositoryImpl @Inject constructor(
    private val api: BuyerApi
) : BuyerRepository {
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

    override fun getDashboardOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val response = api.getDashboardOrders()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getRemoteCart(): Flow<Result<List<CartItemDto>>> = flow {
        try {
            val response = api.getCart()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun addToRemoteCart(productId: String, quantity: Int): Flow<Result<Unit>> = flow {
        try {
            api.addToCart(mapOf("productId" to productId, "quantity" to quantity.toString()))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun removeFromRemoteCart(cartId: Int): Flow<Result<Unit>> = flow {
        try {
            api.removeFromCart(cartId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun clearRemoteCart(): Flow<Result<Unit>> = flow {
        try {
            api.clearCart()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun placeOrder(): Flow<Result<Order>> = flow {
        try {
            val response = api.placeOrder()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            val response = api.getOrderById(orderId)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
