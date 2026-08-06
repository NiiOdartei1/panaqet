package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.SellerApi
import com.example.panaqet.domain.model.*
import com.example.panaqet.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val api: SellerApi
) : SellerRepository {

    override fun getOrdersBySeller(sellerId: String): Flow<Result<List<Order>>> = flow {
        try {
            val response = api.getOrdersBySeller()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateOrderStatus(orderId: String, status: OrderStatus): Flow<Result<Unit>> = flow {
        try {
            api.updateOrderStatus(orderId, mapOf("status" to status.name))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateDeliveryStatus(orderId: String, deliveryStatus: String): Flow<Result<Unit>> = flow {
        try {
            api.updateDeliveryStatus(orderId, mapOf("deliveryStatus" to deliveryStatus))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getStoreProfile(sellerId: String): Flow<Result<StoreProfile>> = flow {
        try {
            val response = api.getStoreProfile()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateStoreProfile(sellerId: String, profile: StoreProfile): Flow<Result<Unit>> = flow {
        try {
            api.updateStoreProfile(profile)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getSellerStats(sellerId: String): Flow<Result<SellerStats>> = flow {
        try {
            val response = api.getSellerStats()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getSubscriptions(): Flow<Result<List<com.example.panaqet.domain.model.Subscription>>> = flow {
        try {
            val response = api.getSubscriptions()
            emit(Result.success(response.map { 
                com.example.panaqet.domain.model.Subscription(it.id, it.name, it.description, it.price, it.validityPeriod, it.features, it.status)
            }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMySubscription(): Flow<Result<com.example.panaqet.domain.model.Subscription>> = flow {
        try {
            val it = api.getMySubscription()
            emit(Result.success(com.example.panaqet.domain.model.Subscription(it.id, it.name, it.description, it.price, it.validityPeriod, it.features, it.status)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun subscribe(planId: Int, validity: Int): Flow<Result<Unit>> = flow {
        try {
            api.subscribe(mapOf("planId" to planId, "validity" to validity))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCommissionPlans(): Flow<Result<List<com.example.panaqet.domain.model.CommissionPlan>>> = flow {
        try {
            val response = api.getCommissionPlans()
            emit(Result.success(response.map { 
                com.example.panaqet.domain.model.CommissionPlan(it.id, it.planName, it.description, it.commissionRate, it.isActive)
            }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun createCommissionPlan(name: String, rate: Double, description: String?): Flow<Result<Unit>> = flow {
        try {
            val body = mutableMapOf("name" to name, "rate" to rate.toString())
            description?.let { body["description"] = it }
            api.createCommissionPlan(body)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun attachCommission(productId: String, planId: Int): Flow<Result<Unit>> = flow {
        try {
            api.attachCommission(productId, mapOf("planId" to planId))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
