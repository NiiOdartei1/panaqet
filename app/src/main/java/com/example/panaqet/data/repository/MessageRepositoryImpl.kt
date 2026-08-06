package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.MessageApi
import com.example.panaqet.domain.model.Conversation
import com.example.panaqet.domain.model.Message
import com.example.panaqet.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: MessageApi
) : MessageRepository {
    override fun getConversations(): Flow<Result<List<Conversation>>> = flow {
        try {
            val response = api.getConversations()
            emit(Result.success(response.map { 
                Conversation(it.id, it.buyerId, it.sellerId, it.productId, it.productName, it.lastMessage, it.timestamp)
            }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMessages(conversationId: String): Flow<Result<List<Message>>> = flow {
        try {
            val response = api.getMessages(conversationId)
            emit(Result.success(response.map { 
                Message(it.id, it.conversationId, it.senderId, it.senderRole, it.content, it.timestamp, it.isRead)
            }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun sendMessage(conversationId: String, content: String): Flow<Result<Unit>> = flow {
        try {
            api.sendMessage(mapOf("conversationId" to conversationId, "content" to content))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
