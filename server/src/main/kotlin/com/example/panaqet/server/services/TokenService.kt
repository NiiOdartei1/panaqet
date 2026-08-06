package com.example.panaqet.server.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object TokenService {
    private const val issuer = "https://panaqet.com/"
    private const val audience = "panaqet-audience"
    private const val secret = "panaqet-secret-key"

    fun generateToken(email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 hours
            .sign(Algorithm.HMAC256(secret))
    }
}
