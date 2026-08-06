package com.example.panaqet.data.remote

import com.example.panaqet.data.remote.dto.*
import retrofit2.http.*

interface MessageApi {
    @GET("messages/conversations")
    suspend fun getConversations(): List<ConversationDto>

    @GET("messages/{id}")
    suspend fun getMessages(@Path("id") conversationId: String): List<MessageDto>

    @POST("messages/send")
    suspend fun sendMessage(@Body request: Map<String, String>)
}
