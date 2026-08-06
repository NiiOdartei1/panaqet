package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.Conversation
import com.example.panaqet.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getConversations(): Flow<Result<List<Conversation>>>
    fun getMessages(conversationId: String): Flow<Result<List<Message>>>
    fun sendMessage(conversationId: String, content: String): Flow<Result<Unit>>
}
