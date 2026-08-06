package com.example.panaqet.data.remote

import com.example.panaqet.data.remote.dto.CartItemDto
import com.example.panaqet.data.remote.dto.ProductDto
import com.example.panaqet.domain.model.Order
import retrofit2.http.*

interface BuyerApi {
    @GET("buyer/wishlist")
    suspend fun getWishlist(): List<ProductDto>

    @POST("buyer/wishlist/{productId}")
    suspend fun addToWishlist(@Path("productId") productId: String)

    @DELETE("buyer/wishlist/{productId}")
    suspend fun removeFromWishlist(@Path("productId") productId: String)

    @GET("buyer/dashboard")
    suspend fun getDashboardOrders(): List<Order>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Order

    @GET("buyer/cart")
    suspend fun getCart(): List<CartItemDto>

    @POST("buyer/cart/add")
    suspend fun addToCart(@Body request: Map<String, String>)

    @POST("buyer/cart/remove/{cartId}")
    suspend fun removeFromCart(@Path("cartId") cartId: Int)

    @POST("buyer/cart/clear")
    suspend fun clearCart()

    @POST("buyer/place")
    suspend fun placeOrder(): Order
}
