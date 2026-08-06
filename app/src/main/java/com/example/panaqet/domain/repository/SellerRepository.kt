package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.OrderStatus
import com.example.panaqet.domain.model.StoreProfile
import kotlinx.coroutines.flow.Flow

interface SellerRepository {
    fun getOrdersBySeller(sellerId: String): Flow<Result<List<Order>>>
    fun updateOrderStatus(orderId: String, status: OrderStatus): Flow<Result<Unit>>
    fun updateDeliveryStatus(orderId: String, deliveryStatus: String): Flow<Result<Unit>>
    fun getStoreProfile(sellerId: String): Flow<Result<StoreProfile>>
    fun updateStoreProfile(sellerId: String, profile: StoreProfile): Flow<Result<Unit>>
    fun getSellerStats(sellerId: String): Flow<Result<com.example.panaqet.domain.model.SellerStats>>
    fun getSubscriptions(): Flow<Result<List<com.example.panaqet.domain.model.Subscription>>>
    fun getMySubscription(): Flow<Result<com.example.panaqet.domain.model.Subscription>>
    fun subscribe(planId: Int, validity: Int): Flow<Result<Unit>>
    fun getCommissionPlans(): Flow<Result<List<com.example.panaqet.domain.model.CommissionPlan>>>
    fun createCommissionPlan(name: String, rate: Double, description: String?): Flow<Result<Unit>>
    fun attachCommission(productId: String, planId: Int): Flow<Result<Unit>>
}
