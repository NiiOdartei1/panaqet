package com.example.panaqet.server.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.example.panaqet.server.database.DatabaseFactory.dbQuery
import com.example.panaqet.server.database.*
import com.example.panaqet.server.models.*
import com.example.panaqet.server.services.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.jetbrains.exposed.sql.*

fun Application.configureRouting() {
    val authRepository = ServerAuthRepository()
    val productRepository = ServerProductRepository()
    val orderRepository = ServerOrderRepository()
    val buyerRepository = ServerBuyerRepository()
    val subscriptionRepository = ServerSubscriptionRepository()
    val commissionRepository = ServerCommissionRepository()
    val messageRepository = ServerMessageRepository()
    val paystackService = PaystackService("sk_test_e69d621029fa90b2fde0eec8d8be4b6ba77fe098")

    routing {
        get("/") {
            call.respondText("PanaQet Backend is running!")
        }
        
        route("/auth") {
            post("/register") {
                val params = call.receive<Map<String, String>>()
                val username = params["username"] ?: params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing username")
                val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing email")
                val password = params["password"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing password")
                val role = params["role"] ?: "BUYER"
                val country = params["country"] ?: "Ghana"
                val phoneNumber = params["phoneNumber"]
                
                val user = authRepository.register(
                    username, email, password, role,
                    country, phoneNumber
                )
                if (user != null) {
                    val token = TokenService.generateToken(email)
                    call.respond(user.copy(token = token))
                } else {
                    call.respond(HttpStatusCode.Conflict, "Email or Username already exists")
                }
            }
            
            post("/login") {
                val params = call.receive<Map<String, String>>()
                val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val password = params["password"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                
                val user = authRepository.login(email, password)
                if (user != null) {
                    val token = TokenService.generateToken(email)
                    call.respond(user.copy(token = token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            }

            authenticate("auth-jwt") {
                get("/store") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        call.respond(StoreProfile(
                            storeName = user.storeName ?: "My Store",
                            address = user.storeAddress,
                            description = user.storeDescription,
                            logoUrl = user.storeLogo
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                post("/store") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val profile = call.receive<StoreProfile>()
                    val success = authRepository.updateStoreProfile(
                        email, profile.storeName, profile.address ?: "", profile.description
                    )
                    if (success) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        
        route("/products") {
            get {
                var products = productRepository.getAllProducts()
                if (products.isEmpty()) {
                    // Seed initial data
                    productRepository.addProduct("Smartphone", "High-end smartphone", 999.99, "https://picsum.photos/200/300", "s1", "Electronics", 10)
                    productRepository.addProduct("Running Shoes", "Comfortable running shoes", 89.50, "https://picsum.photos/201/301", "s2", "Footwear", 50)
                    productRepository.addProduct("Coffee Maker", "Automatic coffee maker", 45.00, "https://picsum.photos/202/302", "s1", "Home", 5)
                    products = productRepository.getAllProducts()
                }
                call.respond(products)
            }
            
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val product = productRepository.getProductById(id)
                if (product != null) {
                    call.respond(product)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            
            authenticate("auth-jwt") {
                post {
                    val product = call.receive<ProductResponse>()
                    val saved = productRepository.addProduct(
                        product.name, product.description, product.price,
                        product.imageUrl ?: "", product.sellerId, product.category ?: "", product.stock,
                        product.condition ?: "New", product.location, product.isPackage
                    )
                    if (saved != null) call.respond(saved) else call.respond(HttpStatusCode.InternalServerError)
                }

                post("/{id}/attach-commission") {
                    val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val params = call.receive<Map<String, Int>>()
                    val planId = params["planId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    commissionRepository.attachPlanToProduct(id, planId)
                    call.respond(HttpStatusCode.OK)
                }

                post("/{id}/availability") {
                    val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val params = call.receive<Map<String, Boolean>>()
                    val isAvailable = params["isAvailable"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    productRepository.updateProductAvailability(id, isAvailable)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        authenticate("auth-jwt") {
            route("/orders") {
                get("/seller") {
                    val sellerId = "s1" // Simplified: In real app, look up user by email to get sellerId
                    val orders = orderRepository.getOrdersBySeller(sellerId)
                    call.respond(orders)
                }

                post("/{id}/status") {
                    val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val params = call.receive<Map<String, String>>()
                    val status = params["status"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    orderRepository.updateOrderStatus(id, status)
                    call.respond(HttpStatusCode.OK)
                }

                get("/{id}") {
                    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val order = orderRepository.getOrderById(id)
                    if (order != null) call.respond(order) else call.respond(HttpStatusCode.NotFound)
                }

                post("/place") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        val cartItems = buyerRepository.getCart(user.id)
                        if (cartItems.isEmpty()) {
                            return@post call.respond(HttpStatusCode.BadRequest, "Cart is empty")
                        }
                        val totalAmount = cartItems.sumOf { it.price * it.quantity }
                        val buyer = dbQuery { Buyers.select { Buyers.userId eq user.id }.singleOrNull() }
                        if (buyer == null) return@post call.respond(HttpStatusCode.NotFound, "Buyer profile not found")
                        
                        val order = orderRepository.createOrder(
                            buyerId = buyer[Buyers.id],
                            totalAmount = totalAmount,
                            items = cartItems.map { OrderItemRequest(it.productId, it.quantity, it.price * it.quantity) }
                        )
                        
                        if (order != null) {
                            buyerRepository.clearCart(user.id)
                            call.respond(order)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                get("/stats") {
                    val sellerId = "s1" // Simplified
                    val stats = orderRepository.getSellerStats(sellerId)
                    call.respond(stats)
                }

                route("/subscriptions") {
                    get {
                        val plans = subscriptionRepository.getSubscriptions()
                        call.respond(plans)
                    }
                    
                    get("/my") {
                        val sellerId = "s1" // Simplified
                        val plan = subscriptionRepository.getSellerSubscription(sellerId)
                        if (plan != null) call.respond(plan) else call.respond(HttpStatusCode.NotFound)
                    }

                    post("/subscribe") {
                        val params = call.receive<Map<String, Int>>()
                        val planId = params["planId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val validity = params["validity"] ?: 30
                        val sellerId = "s1" // Simplified
                        subscriptionRepository.subscribeSeller(sellerId, planId, validity)
                        call.respond(HttpStatusCode.OK)
                    }
                }

                route("/commissions") {
                    get {
                        val sellerId = "s1"
                        val plans = commissionRepository.getCommissionPlans(sellerId)
                        call.respond(plans)
                    }
                    post {
                        val params = call.receive<Map<String, String>>()
                        val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val rate = params["rate"]?.toDoubleOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val desc = params["description"]
                        val sellerId = "s1"
                        commissionRepository.createCommissionPlan(sellerId, name, rate, desc)
                        call.respond(HttpStatusCode.Created)
                    }
                }
            }

            route("/messages") {
                get("/conversations") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        val conversations = messageRepository.getConversations(user.id)
                        call.respond(conversations)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                get("/{id}") {
                    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val messages = messageRepository.getMessages(id)
                    call.respond(messages)
                }

                post("/send") {
                    val params = call.receive<Map<String, String>>()
                    val conversationId = params["conversationId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val content = params["content"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        messageRepository.sendMessage(conversationId, user.id, user.role, content)
                        call.respond(HttpStatusCode.Created)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }

            route("/buyer") {
                get("/dashboard") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        val orders = buyerRepository.getBuyerDashboard(user.id)
                        call.respond(orders)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                route("/wishlist") {
                    get {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            val wishlist = buyerRepository.getWishlist(user.id)
                            call.respond(wishlist)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    post("/{productId}") {
                        val productId = call.parameters["productId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            buyerRepository.saveToWishlist(user.id, productId)
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    delete("/{productId}") {
                        val productId = call.parameters["productId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            buyerRepository.removeFromWishlist(user.id, productId)
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }

                route("/cart") {
                    get {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            val cart = buyerRepository.getCart(user.id)
                            call.respond(cart)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    post("/add") {
                        val params = call.receive<Map<String, String>>()
                        val productId = params["productId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val quantity = params["quantity"]?.toIntOrNull() ?: 1
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            buyerRepository.addToCart(user.id, productId, quantity)
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    post("/remove/{cartId}") {
                        val cartId = call.parameters["cartId"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                        buyerRepository.removeFromCart(cartId)
                        call.respond(HttpStatusCode.OK)
                    }

                    post("/clear") {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal!!.payload.getClaim("email").asString()
                        val user = authRepository.getStoreProfile(email)
                        if (user != null) {
                            buyerRepository.clearCart(user.id)
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }

                get("/receipt/{orderId}") {
                    val orderId = call.parameters["orderId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    val user = authRepository.getStoreProfile(email)
                    if (user != null) {
                        val order = buyerRepository.getBuyerDashboard(user.id).find { it.id == orderId }
                        if (order != null) {
                            val receiptText = buildString {
                                appendLine("PanaQet Receipt")
                                appendLine("Order ID: ${order.id}")
                                appendLine("Date: ${order.timestamp}")
                                appendLine("Total: GH₵ ${order.totalAmount}")
                                appendLine("Items:")
                                order.items.forEach { item ->
                                    appendLine("- ${item.productName} x${item.quantity}: GH₵ ${item.totalPrice}")
                                }
                            }
                            call.respondText(receiptText)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
        
        authenticate("auth-jwt") {
            route("/payment") {
                post("/initialize") {
                    val params = call.receive<Map<String, String>>()
                    val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val amount = params["amount"]?.toDoubleOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    val response = paystackService.initializeTransaction(email, amount)
                    if (response != null && response.status) {
                        call.respond(PaymentInitializationResponse(
                            accessCode = response.data.access_code,
                            reference = response.data.reference
                        ))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to initialize payment")
                    }
                }
            }
        }
    }
}
