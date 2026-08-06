package com.example.panaqet.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("payment/initialize")
    suspend fun initializePayment(@Body request: Map<String, String>): PaymentInitializationDto
}

@Serializable
data class PaymentInitializationDto(
    val accessCode: String,
    val reference: String
)
