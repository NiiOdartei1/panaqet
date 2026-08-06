package com.example.panaqet.data.remote

import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.StoreProfile
import com.example.panaqet.data.remote.dto.*
import retrofit2.http.*

interface SellerApi {
    @GET("orders/seller")
    suspend fun getOrdersBySeller(): List<Order>

    @GET("orders/stats")
    suspend fun getSellerStats(): com.example.panaqet.domain.model.SellerStats

    @POST("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: String, @Body request: Map<String, String>)

    @POST("orders/{id}/delivery")
    suspend fun updateDeliveryStatus(@Path("id") id: String, @Body request: Map<String, String>)

    @GET("auth/store")
    suspend fun getStoreProfile(): StoreProfile

    @POST("auth/store")
    suspend fun updateStoreProfile(@Body profile: StoreProfile)

    @GET("orders/subscriptions")
    suspend fun getSubscriptions(): List<SubscriptionDto>

    @GET("orders/subscriptions/my")
    suspend fun getMySubscription(): SubscriptionDto

    @POST("orders/subscriptions/subscribe")
    suspend fun subscribe(@Body request: Map<String, Int>)

    @GET("orders/commissions")
    suspend fun getCommissionPlans(): List<CommissionPlanDto>

    @POST("orders/commissions")
    suspend fun createCommissionPlan(@Body request: Map<String, String>)

    @POST("products/{id}/attach-commission")
    suspend fun attachCommission(@Path("id") productId: String, @Body request: Map<String, Int>)
}
