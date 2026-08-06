package com.example.panaqet.data.repository

import com.example.panaqet.data.local.CartDao
import com.example.panaqet.data.local.entity.CartItemEntity
import com.example.panaqet.domain.model.CartItem
import com.example.panaqet.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val dao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return dao.getCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(item: CartItem) {
        dao.insertCartItem(item.toEntity())
    }

    override suspend fun removeFromCart(item: CartItem) {
        dao.deleteCartItem(item.toEntity())
    }

    override suspend fun updateQuantity(id: String, quantity: Int) {
        dao.updateQuantity(id, quantity)
    }

    override suspend fun clearCart() {
        dao.clearCart()
    }

    private fun CartItemEntity.toDomain(): CartItem {
        return CartItem(id, name, price, imageUrl, quantity)
    }

    private fun CartItem.toEntity(): CartItemEntity {
        return CartItemEntity(id, name, price, imageUrl, quantity)
    }
}
