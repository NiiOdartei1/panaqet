package com.example.panaqet.server.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

object Seeder {
    fun seedSubscriptionPlans() {
        val plans = listOf(
            SubscriptionPlanData(
                name = "Basic Plan",
                description = "Ideal for new sellers just starting out.",
                price = 50.0,
                validityPeriod = 30,
                features = buildJsonObject {
                    put("max_products", 10)
                    put("support", "Email only")
                    put("analytics", "Basic (views and clicks)")
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Standard Plan",
                description = "For growing sellers who want more visibility.",
                price = 120.0,
                validityPeriod = 90,
                features = buildJsonObject {
                    put("max_products", 50)
                    put("support", "Email and chat")
                    put("analytics", "Advanced (views, clicks, conversions)")
                    put("priority_placement", true)
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Premium Plan",
                description = "Best for established sellers looking to scale.",
                price = 300.0,
                validityPeriod = 180,
                features = buildJsonObject {
                    put("max_products", "Unlimited")
                    put("support", "Dedicated account manager")
                    put("analytics", "Full suite (views, clicks, conversions, revenue tracking)")
                    put("promoted_products", true)
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Seasonal Boost Plan",
                description = "Perfect for sellers aiming for seasonal sales spikes.",
                price = 150.0,
                validityPeriod = 30,
                features = buildJsonObject {
                    put("additional_products", 20)
                    put("priority_placement", "Seasonal")
                    put("social_media_promotions", true)
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Enterprise Plan",
                description = "Designed for large-scale sellers with extensive needs.",
                price = 2000.0,
                validityPeriod = 365,
                features = buildJsonObject {
                    put("max_products", "Unlimited")
                    put("support", "Multi-user and API access")
                    put("analytics", "Custom insights")
                    put("personalized_promotions", true)
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Trial Plan",
                description = "A free or low-cost option to attract new sellers.",
                price = 0.0,
                validityPeriod = 14,
                features = buildJsonObject {
                    put("max_products", 5)
                    put("support", "None")
                    put("analytics", "Basic (views and clicks)")
                }.toString()
            ),
            SubscriptionPlanData(
                name = "Pay-Per-Feature Plan",
                description = "Flexible pricing based on additional features.",
                price = 50.0,
                validityPeriod = 30,
                features = buildJsonObject {
                    put("base_benefits", "Basic Plan")
                    putJsonArray("add_ons") {
                        add(kotlinx.serialization.json.JsonPrimitive("Extra listings"))
                        add(kotlinx.serialization.json.JsonPrimitive("Priority placement"))
                        add(kotlinx.serialization.json.JsonPrimitive("Advanced analytics"))
                    }
                }.toString()
            )
        )

        transaction {
            for (plan in plans) {
                val exists = Subscriptions.select { Subscriptions.name eq plan.name }.any()
                if (!exists) {
                    Subscriptions.insert {
                        it[Subscriptions.name] = plan.name
                        it[Subscriptions.description] = plan.description
                        it[Subscriptions.price] = plan.price
                        it[Subscriptions.validityPeriod] = plan.validityPeriod
                        it[Subscriptions.features] = plan.features
                        it[Subscriptions.status] = "active"
                    }
                }
            }
        }
    }
}

data class SubscriptionPlanData(
    val name: String,
    val description: String,
    val price: Double,
    val validityPeriod: Int,
    val features: String
)
