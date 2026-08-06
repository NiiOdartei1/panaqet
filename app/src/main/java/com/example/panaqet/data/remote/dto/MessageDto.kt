package com.example.panaqet.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConversationDto(
    val id: String,
    val buyerId: String,
    val sellerId: String,
    val productId: String,
    val productName: String?,
    val lastMessage: String?,
    val timestamp: String
)

@Serializable
data class MessageDto(
    val id: Int,
    val conversationId: String,
    val senderId: String,
    val senderRole: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)
