package com.example.panaqet.server.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val id = varchar("id", 50)
    val username = varchar("username", 64).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 300)
    val role = varchar("role", 20)
    val countryCode = varchar("countryCode", 5).nullable()
    val phoneNumber = varchar("phoneNumber", 15).nullable().uniqueIndex()
    val country = varchar("country", 100)
    val dateJoined = datetime("dateJoined").default(LocalDateTime.now())
    val profileImage = varchar("profileImage", 255).default("default_profile.png")
    val userTheme = varchar("userTheme", 50).default("light")
    val preferredLanguage = varchar("preferredLanguage", 10).default("en")
    val idType = varchar("idType", 50).nullable()
    val idFrontImage = varchar("idFrontImage", 255).nullable()
    val idBackImage = varchar("idBackImage", 255).nullable()
    val signupComplete = bool("signupComplete").default(false)
    val isFirstLogin = bool("isFirstLogin").default(true)
    val storeName = varchar("storeName", 100).nullable()
    val storeAddress = varchar("storeAddress", 255).nullable()
    val storeDescription = varchar("storeDescription", 255).nullable()
    val storeLogo = varchar("storeLogo", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Sellers : Table("sellers") {
    val id = varchar("id", 50)
    val userId = reference("userId", Users.id)
    val username = varchar("username", 64)
    val email = varchar("email", 100)
    val location = varchar("location", 120).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Buyers : Table("buyers") {
    val id = varchar("id", 50)
    val userId = reference("userId", Users.id)
    val username = varchar("username", 64)
    val email = varchar("email", 100)

    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = varchar("id", 50)
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val price = double("price")
    val stock = integer("stock").default(0)
    val sellerId = reference("sellerId", Sellers.id)
    val dateAdded = datetime("dateAdded").default(LocalDateTime.now())
    val status = varchar("status", 50).default("Pending")
    val category = varchar("category", 100).nullable()
    val location = varchar("location", 200).nullable()
    val condition = varchar("condition", 50).nullable()
    val brand = varchar("brand", 150).nullable()
    val gender = varchar("gender", 150).nullable()
    val color = varchar("color", 150).nullable()
    val size = varchar("size", 150).nullable()
    val isPackage = bool("isPackage").default(false)
    val qrCode = varchar("qrCode", 255).nullable()
    val viewCount = integer("viewCount").default(0)
    val isAvailable = bool("isAvailable").default(true)
    val imageUrl = varchar("imageUrl", 255).nullable() // Legacy support or primary image

    override val primaryKey = PrimaryKey(id)
}

object ProductImages : Table("product_images") {
    val id = integer("id").autoIncrement()
    val productId = reference("productId", Products.id)
    val imageUrl = varchar("imageUrl", 255)

    override val primaryKey = PrimaryKey(id)
}

object ProductComponents : Table("product_components") {
    val id = integer("id").autoIncrement()
    val productId = reference("productId", Products.id)
    val name = varchar("name", 100)
    val price = double("price")
    val imageUrl = varchar("imageUrl", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Orders : Table("orders") {
    val id = varchar("id", 50)
    val buyerId = reference("buyerId", Buyers.id)
    val totalAmount = double("totalAmount")
    val orderDate = datetime("orderDate").default(LocalDateTime.now())
    val status = varchar("status", 20).default("PENDING")
    val deliveryStatus = varchar("deliveryStatus", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = reference("orderId", Orders.id)
    val productId = reference("productId", Products.id)
    val quantity = integer("quantity")
    val totalPrice = double("totalPrice")

    override val primaryKey = PrimaryKey(id)
}

object Subscriptions : Table("subscriptions") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val description = varchar("description", 500).nullable()
    val price = double("price")
    val validityPeriod = integer("validityPeriod").default(30)
    val features = text("features").nullable() // JSON as text for simplicity
    val dateAdded = datetime("dateAdded").default(LocalDateTime.now())
    val status = varchar("status", 20).default("inactive")

    override val primaryKey = PrimaryKey(id)
}

object SellerSubscriptions : Table("seller_subscriptions") {
    val sellerId = reference("sellerId", Sellers.id)
    val subscriptionId = reference("subscriptionId", Subscriptions.id)
    val subscribedOn = datetime("subscribedOn").default(LocalDateTime.now())
    val validUntil = datetime("validUntil")

    override val primaryKey = PrimaryKey(sellerId, subscriptionId)
}

object CommissionPlans : Table("commission_plans") {
    val id = integer("id").autoIncrement()
    val planName = varchar("planName", 100)
    val description = varchar("description", 500).nullable()
    val commissionRate = double("commissionRate")
    val sellerId = reference("sellerId", Sellers.id)
    val dateCreated = datetime("dateCreated").default(LocalDateTime.now())
    val isActive = bool("isActive").default(true)

    override val primaryKey = PrimaryKey(id)
}

object ProductCommissions : Table("product_commissions") {
    val productId = reference("productId", Products.id)
    val commissionPlanId = reference("commissionPlanId", CommissionPlans.id)

    override val primaryKey = PrimaryKey(productId, commissionPlanId)
}

object Conversations : Table("conversations") {
    val id = varchar("id", 50)
    val buyerId = reference("buyerId", Users.id)
    val sellerId = reference("sellerId", Users.id)
    val productId = reference("productId", Products.id)
    val createdAt = datetime("createdAt").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object Messages : Table("messages") {
    val id = integer("id").autoIncrement()
    val conversationId = reference("conversationId", Conversations.id)
    val senderId = reference("senderId", Users.id)
    val senderRole = varchar("senderRole", 10) // 'buyer' or 'seller'
    val content = text("content")
    val timestamp = datetime("timestamp").default(LocalDateTime.now())
    val isRead = bool("isRead").default(false)

    override val primaryKey = PrimaryKey(id)
}

object Cart : Table("cart") {
    val id = integer("id").autoIncrement()
    val userId = reference("userId", Users.id)
    val productId = reference("productId", Products.id)
    val componentId = integer("componentId").nullable() // Reference to ProductComponents.id
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)
}

object SavedProducts : Table("saved_products") {
    val id = integer("id").autoIncrement()
    val userId = reference("userId", Users.id)
    val productId = reference("productId", Products.id)
    val dateSaved = datetime("dateSaved").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object Referrals : Table("referrals") {
    val id = integer("id").autoIncrement()
    val affiliateId = varchar("affiliateId", 50) // Reference to User.id or separate Affiliate table
    val productId = reference("productId", Products.id)
    val orderId = reference("orderId", Orders.id).nullable()
    val commission = double("commission")
    val timestamp = datetime("timestamp").default(LocalDateTime.now())
    val status = varchar("status", 20).default("pending")

    override val primaryKey = PrimaryKey(id)
}
