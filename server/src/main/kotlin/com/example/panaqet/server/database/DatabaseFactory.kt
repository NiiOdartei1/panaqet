package com.example.panaqet.server.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(
                Users, Sellers, Buyers, Products, ProductImages, ProductComponents,
                Orders, OrderItems, Subscriptions, SellerSubscriptions,
                CommissionPlans, ProductCommissions, Conversations, Messages, Cart,
                SavedProducts, Referrals
            )
            Seeder.seedSubscriptionPlans()
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
