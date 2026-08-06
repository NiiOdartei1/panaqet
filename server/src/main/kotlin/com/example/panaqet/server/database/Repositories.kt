package com.example.panaqet.server.database

import com.example.panaqet.server.database.DatabaseFactory.dbQuery
import com.example.panaqet.server.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

class ServerAuthRepository {
    suspend fun register(
        username: String, email: String, passwordHash: String, role: String,
        country: String, phoneNumber: String? = null
    ): UserResponse? = dbQuery {
        val id = UUID.randomUUID().toString()
        val insertStatement = Users.insert {
            it[Users.id] = id
            it[Users.username] = username
            it[Users.email] = email
            it[Users.password] = passwordHash
            it[Users.role] = role
            it[Users.country] = country
            it[Users.phoneNumber] = phoneNumber
        }
        val user = insertStatement.resultedValues?.singleOrNull()?.toUserResponse()
        
        if (user != null) {
            val sellerOrBuyerId = UUID.randomUUID().toString()
            if (role == "SELLER") {
                Sellers.insert {
                    it[Sellers.id] = sellerOrBuyerId
                    it[Sellers.userId] = user.id
                    it[Sellers.username] = user.username
                    it[Sellers.email] = user.email
                }
            } else {
                Buyers.insert {
                    it[Buyers.id] = sellerOrBuyerId
                    it[Buyers.userId] = user.id
                    it[Buyers.username] = user.username
                    it[Buyers.email] = user.email
                }
            }
        }
        user
    }

    suspend fun login(email: String, passwordHash: String): UserResponse? = dbQuery {
        Users.select { (Users.email eq email) and (Users.password eq passwordHash) }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    suspend fun getStoreProfile(email: String): UserResponse? = dbQuery {
        Users.select { Users.email eq email }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    suspend fun updateStoreProfile(email: String, storeName: String, storeAddress: String, description: String?): Boolean = dbQuery {
        Users.update({ Users.email eq email }) {
            it[Users.storeName] = storeName
            it[Users.storeAddress] = storeAddress
            it[Users.storeDescription] = description
        } > 0
    }

    private fun ResultRow.toUserResponse() = UserResponse(
        id = this[Users.id],
        username = this[Users.username],
        email = this[Users.email],
        role = this[Users.role],
        countryCode = this[Users.countryCode],
        phoneNumber = this[Users.phoneNumber],
        country = this[Users.country],
        profileImage = this[Users.profileImage],
        userTheme = this[Users.userTheme],
        preferredLanguage = this[Users.preferredLanguage],
        idType = this[Users.idType],
        signupComplete = this[Users.signupComplete],
        isFirstLogin = this[Users.isFirstLogin],
        storeName = this[Users.storeName],
        storeAddress = this[Users.storeAddress],
        storeDescription = this[Users.storeDescription],
        storeLogo = this[Users.storeLogo]
    )
}

class ServerProductRepository {
    suspend fun getAllProducts(): List<ProductResponse> = dbQuery {
        Products.selectAll().map { it.toProductResponseSync() }
    }

    suspend fun getProductById(id: String): ProductResponse? = dbQuery {
        Products.select { Products.id eq id }
            .map { it.toProductResponseSync() }
            .singleOrNull()
    }

    suspend fun addProduct(
        name: String, description: String?, price: Double,
        imageUrl: String, sellerId: String, category: String, stock: Int,
        condition: String = "New", location: String? = null, isPackage: Boolean = false
    ): ProductResponse? = dbQuery {
        val id = UUID.randomUUID().toString()
        val insertStatement = Products.insert {
            it[Products.id] = id
            it[Products.name] = name
            it[Products.description] = description
            it[Products.price] = price
            it[Products.imageUrl] = imageUrl
            it[Products.sellerId] = sellerId
            it[Products.category] = category
            it[Products.stock] = stock
            it[Products.condition] = condition
            it[Products.location] = location
            it[Products.isPackage] = isPackage
        }
        insertStatement.resultedValues?.singleOrNull()?.toProductResponseSync()
    }

    suspend fun updateProductAvailability(id: String, isAvailable: Boolean) = dbQuery {
        Products.update({ Products.id eq id }) {
            it[Products.isAvailable] = isAvailable
        }
    }

    private fun ResultRow.toProductResponseSync(): ProductResponse {
        val prodId = this[Products.id]
        val images = ProductImages.select { ProductImages.productId eq prodId }
            .map { 
                ProductImageResponse(
                    id = it[ProductImages.id],
                    productId = it[ProductImages.productId],
                    imageUrl = it[ProductImages.imageUrl]
                )
            }
        val components = ProductComponents.select { ProductComponents.productId eq prodId }
            .map { 
                ProductComponentResponse(
                    id = it[ProductComponents.id],
                    productId = it[ProductComponents.productId],
                    name = it[ProductComponents.name],
                    price = it[ProductComponents.price],
                    imageUrl = it[ProductComponents.imageUrl]
                )
            }

        return ProductResponse(
            id = prodId,
            name = this[Products.name],
            description = this[Products.description],
            price = this[Products.price],
            sellerId = this[Products.sellerId],
            category = this[Products.category],
            stock = this[Products.stock],
            condition = this[Products.condition],
            location = this[Products.location],
            brand = this[Products.brand],
            gender = this[Products.gender],
            color = this[Products.color],
            size = this[Products.size],
            isPackage = this[Products.isPackage],
            qrCode = this[Products.qrCode],
            viewCount = this[Products.viewCount],
            isAvailable = this[Products.isAvailable],
            imageUrl = this[Products.imageUrl],
            images = images,
            components = components
        )
    }
}

class ServerOrderRepository {
    suspend fun getOrdersBySeller(sellerId: String): List<OrderResponse> = dbQuery {
        // Simplified: Fetching orders where sellerId is present in any OrderItem.productId -> Products.sellerId
        // But the Orders table itself should probably have a reference or we join.
        // Python reference: orders = db.session.query(Order, Product).join(OrderItem...)...
        
        // Let's assume Orders link to Buyers. To find orders for a seller, we look at OrderItems.
        val orderIds = OrderItems.join(Products, JoinType.INNER, additionalConstraint = { OrderItems.productId eq Products.id })
            .select { Products.sellerId eq sellerId }
            .map { it[OrderItems.orderId] }
            .distinct()
        
        Orders.select { Orders.id inList orderIds }
            .map { it.toOrderResponseSync() }
    }

    suspend fun updateOrderStatus(orderId: String, status: String, deliveryStatus: String? = null) = dbQuery {
        Orders.update({ Orders.id eq orderId }) {
            it[Orders.status] = status
            if (deliveryStatus != null) {
                it[Orders.deliveryStatus] = deliveryStatus
            }
        }
    }

    suspend fun createOrder(
        buyerId: String, totalAmount: Double, items: List<OrderItemRequest>
    ): OrderResponse? = dbQuery {
        val orderId = UUID.randomUUID().toString()
        Orders.insert {
            it[Orders.id] = orderId
            it[Orders.buyerId] = buyerId
            it[Orders.totalAmount] = totalAmount
        }
        
        items.forEach { item ->
            OrderItems.insert {
                it[OrderItems.orderId] = orderId
                it[OrderItems.productId] = item.productId
                it[OrderItems.quantity] = item.quantity
                it[OrderItems.totalPrice] = item.totalPrice
            }
        }
        
        Orders.select { Orders.id eq orderId }.singleOrNull()?.toOrderResponseSync()
    }

    suspend fun getSellerStats(sellerId: String) = dbQuery {
        val orderIds = OrderItems.join(Products, JoinType.INNER, additionalConstraint = { OrderItems.productId eq Products.id })
            .select { Products.sellerId eq sellerId }
            .map { it[OrderItems.orderId] }
            .distinct()

        val sales = Orders.select { (Orders.id inList orderIds) and (Orders.status eq "DELIVERED") }
            .sumOf { it[Orders.totalAmount] }
        
        val productsCount = Products.select { Products.sellerId eq sellerId }.count().toInt()
        val activeProductsCount = Products.select { (Products.sellerId eq sellerId) and (Products.isAvailable eq true) }.count().toInt()
        val pendingOrdersCount = Orders.select { (Orders.id inList orderIds) and (Orders.status eq "PENDING") }.count().toInt()

        SellerStats(
            totalSales = sales,
            totalProducts = productsCount,
            pendingOrders = pendingOrdersCount,
            activeProducts = activeProductsCount
        )
    }

    suspend fun getOrderById(orderId: String): OrderResponse? = dbQuery {
        Orders.select { Orders.id eq orderId }
            .map { it.toOrderResponseSync() }
            .singleOrNull()
    }

    private fun ResultRow.toOrderResponseSync(): OrderResponse {
        val orderId = this[Orders.id]
        val items = OrderItems.join(Products, JoinType.INNER, additionalConstraint = { OrderItems.productId eq Products.id })
            .select { OrderItems.orderId eq orderId }
            .map {
                OrderItemResponse(
                    id = it[OrderItems.id],
                    orderId = it[OrderItems.orderId],
                    productId = it[OrderItems.productId],
                    quantity = it[OrderItems.quantity],
                    totalPrice = it[OrderItems.totalPrice],
                    productName = it[Products.name]
                )
            }
        
        return OrderResponse(
            id = orderId,
            buyerId = this[Orders.buyerId],
            totalAmount = this[Orders.totalAmount],
            status = this[Orders.status],
            deliveryStatus = this[Orders.deliveryStatus],
            timestamp = this[Orders.orderDate].toString(),
            items = items
        )
    }
}

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
    val totalPrice: Double
)

class ServerSubscriptionRepository {
    suspend fun getSubscriptions(): List<SubscriptionResponse> = dbQuery {
        Subscriptions.selectAll().map {
            SubscriptionResponse(
                id = it[Subscriptions.id],
                name = it[Subscriptions.name],
                description = it[Subscriptions.description],
                price = it[Subscriptions.price],
                validityPeriod = it[Subscriptions.validityPeriod],
                features = it[Subscriptions.features],
                status = it[Subscriptions.status]
            )
        }
    }

    suspend fun subscribeSeller(sellerId: String, subscriptionId: Int, validityDays: Int) = dbQuery {
        SellerSubscriptions.insert {
            it[SellerSubscriptions.sellerId] = sellerId
            it[SellerSubscriptions.subscriptionId] = subscriptionId
            it[SellerSubscriptions.validUntil] = LocalDateTime.now().plusDays(validityDays.toLong())
        }
    }

    suspend fun getSellerSubscription(sellerId: String): SubscriptionResponse? = dbQuery {
        SellerSubscriptions.join(Subscriptions, JoinType.INNER, additionalConstraint = { SellerSubscriptions.subscriptionId eq Subscriptions.id })
            .select { SellerSubscriptions.sellerId eq sellerId }
            .map {
                SubscriptionResponse(
                    id = it[Subscriptions.id],
                    name = it[Subscriptions.name],
                    description = it[Subscriptions.description],
                    price = it[Subscriptions.price],
                    validityPeriod = it[Subscriptions.validityPeriod],
                    features = it[Subscriptions.features],
                    status = it[Subscriptions.status]
                )
            }.singleOrNull()
    }
}

class ServerCommissionRepository {
    suspend fun getCommissionPlans(sellerId: String): List<CommissionPlanResponse> = dbQuery {
        CommissionPlans.select { CommissionPlans.sellerId eq sellerId }.map {
            CommissionPlanResponse(
                id = it[CommissionPlans.id],
                planName = it[CommissionPlans.planName],
                description = it[CommissionPlans.description],
                commissionRate = it[CommissionPlans.commissionRate],
                isActive = it[CommissionPlans.isActive]
            )
        }
    }

    suspend fun createCommissionPlan(sellerId: String, planName: String, rate: Double, description: String?) = dbQuery {
        CommissionPlans.insert {
            it[CommissionPlans.sellerId] = sellerId
            it[CommissionPlans.planName] = planName
            it[CommissionPlans.commissionRate] = rate
            it[CommissionPlans.description] = description
        }
    }

    suspend fun attachPlanToProduct(productId: String, planId: Int) = dbQuery {
        ProductCommissions.insert {
            it[ProductCommissions.productId] = productId
            it[ProductCommissions.commissionPlanId] = planId
        }
    }
}

class ServerMessageRepository {
    suspend fun getConversations(userId: String): List<ConversationResponse> = dbQuery {
        Conversations.select { (Conversations.buyerId eq userId) or (Conversations.sellerId eq userId) }
            .map { it.toConversationResponse() }
    }

    suspend fun getMessages(conversationId: String): List<MessageResponse> = dbQuery {
        Messages.select { Messages.conversationId eq conversationId }
            .orderBy(Messages.timestamp, SortOrder.ASC)
            .map { it.toMessageResponse() }
    }

    suspend fun sendMessage(conversationId: String, senderId: String, senderRole: String, content: String) = dbQuery {
        Messages.insert {
            it[Messages.conversationId] = conversationId
            it[Messages.senderId] = senderId
            it[Messages.senderRole] = senderRole
            it[Messages.content] = content
            it[Messages.timestamp] = LocalDateTime.now()
        }
    }

    private fun ResultRow.toConversationResponse() = ConversationResponse(
        id = this[Conversations.id],
        buyerId = this[Conversations.buyerId],
        sellerId = this[Conversations.sellerId],
        productId = this[Conversations.productId],
        productName = null, // In real app, join with Products
        lastMessage = null, // In real app, fetch last message
        timestamp = this[Conversations.createdAt].toString()
    )

    private fun ResultRow.toMessageResponse() = MessageResponse(
        id = this[Messages.id],
        conversationId = this[Messages.conversationId],
        senderId = this[Messages.senderId],
        senderRole = this[Messages.senderRole],
        content = this[Messages.content],
        timestamp = this[Messages.timestamp].toString(),
        isRead = this[Messages.isRead]
    )
}

class ServerBuyerRepository {
    suspend fun getWishlist(userId: String): List<ProductResponse> = dbQuery {
        val productIds = SavedProducts.select { SavedProducts.userId eq userId }
            .map { it[SavedProducts.productId] }
        
        Products.select { Products.id inList productIds }
            .map { it.toProductResponseSync() }
    }

    suspend fun saveToWishlist(userId: String, productId: String) = dbQuery {
        val exists = SavedProducts.select { (SavedProducts.userId eq userId) and (SavedProducts.productId eq productId) }.any()
        if (!exists) {
            SavedProducts.insert {
                it[SavedProducts.userId] = userId
                it[SavedProducts.productId] = productId
                it[SavedProducts.dateSaved] = LocalDateTime.now()
            }
        }
    }

    suspend fun removeFromWishlist(userId: String, productId: String) = dbQuery {
        SavedProducts.deleteWhere { (SavedProducts.userId eq userId) and (SavedProducts.productId eq productId) }
    }

    suspend fun getCart(userId: String): List<CartItemResponse> = dbQuery {
        Cart.join(Products, JoinType.INNER, additionalConstraint = { Cart.productId eq Products.id })
            .select { Cart.userId eq userId }
            .map {
                CartItemResponse(
                    id = it[Cart.id],
                    productId = it[Cart.productId],
                    productName = it[Products.name],
                    price = it[Products.price],
                    quantity = it[Cart.quantity],
                    imageUrl = it[Products.imageUrl]
                )
            }
    }

    suspend fun addToCart(userId: String, productId: String, quantity: Int) = dbQuery {
        val existing = Cart.select { (Cart.userId eq userId) and (Cart.productId eq productId) }.singleOrNull()
        if (existing != null) {
            Cart.update({ (Cart.userId eq userId) and (Cart.productId eq productId) }) {
                it[Cart.quantity] = existing[Cart.quantity] + quantity
            }
        } else {
            Cart.insert {
                it[Cart.userId] = userId
                it[Cart.productId] = productId
                it[Cart.quantity] = quantity
            }
        }
    }

    suspend fun removeFromCart(cartId: Int) = dbQuery {
        Cart.deleteWhere { Cart.id eq cartId }
    }

    suspend fun clearCart(userId: String) = dbQuery {
        Cart.deleteWhere { Cart.userId eq userId }
    }

    suspend fun getBuyerDashboard(userId: String): List<OrderResponse> = dbQuery {
        // Buyers table uses separate ID, let's find buyerId linked to userId
        val buyerId = Buyers.select { Buyers.userId eq userId }.singleOrNull()?.get(Buyers.id)
            ?: return@dbQuery emptyList<OrderResponse>()

        Orders.select { Orders.buyerId eq buyerId }
            .map { it.toOrderResponseSync() }
    }
    
    // Extension function needed for ResultRow
    private fun ResultRow.toProductResponseSync(): ProductResponse {
        // Reusing logic from ServerProductRepository (maybe move to companion or util)
        // For now, minimal mapping
        return ProductResponse(
            id = this[Products.id],
            name = this[Products.name],
            description = this[Products.description],
            price = this[Products.price],
            sellerId = this[Products.sellerId],
            category = this[Products.category],
            stock = this[Products.stock],
            condition = this[Products.condition],
            location = this[Products.location],
            brand = this[Products.brand],
            gender = this[Products.gender],
            color = this[Products.color],
            size = this[Products.size],
            isPackage = this[Products.isPackage],
            qrCode = this[Products.qrCode],
            viewCount = this[Products.viewCount],
            isAvailable = this[Products.isAvailable],
            imageUrl = this[Products.imageUrl]
        )
    }

    private fun ResultRow.toOrderResponseSync(): OrderResponse {
        val orderId = this[Orders.id]
        val items = OrderItems.join(Products, JoinType.INNER, additionalConstraint = { OrderItems.productId eq Products.id })
            .select { OrderItems.orderId eq orderId }
            .map {
                OrderItemResponse(
                    id = it[OrderItems.id],
                    orderId = it[OrderItems.orderId],
                    productId = it[OrderItems.productId],
                    quantity = it[OrderItems.quantity],
                    totalPrice = it[OrderItems.totalPrice],
                    productName = it[Products.name]
                )
            }
        
        return OrderResponse(
            id = orderId,
            buyerId = this[Orders.buyerId],
            totalAmount = this[Orders.totalAmount],
            status = this[Orders.status],
            deliveryStatus = this[Orders.deliveryStatus],
            timestamp = this[Orders.orderDate].toString(),
            items = items
        )
    }
}

@Serializable
data class CartItemResponse(
    val id: Int,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?
)
