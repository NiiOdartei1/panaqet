package com.example.panaqet.server.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PaystackService(private val secretKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun initializeTransaction(email: String, amount: Double): PaystackInitResponse? {
        val amountInPesewas = (amount * 100).toInt()
        
        return try {
            val response = client.post("https://api.paystack.co/transaction/initialize") {
                header(HttpHeaders.Authorization, "Bearer $secretKey")
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "email" to email,
                    "amount" to amountInPesewas.toString(),
                    "currency" to "GHS"
                ))
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<PaystackInitResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class PaystackInitResponse(
    val status: Boolean,
    val message: String,
    val data: PaystackInitData
)

@Serializable
data class PaystackInitData(
    val authorization_url: String,
    val access_code: String,
    val reference: String
)
