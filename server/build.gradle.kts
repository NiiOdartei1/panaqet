plugins {
    id("org.jetbrains.kotlin.jvm")
    id("application")
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example.panaqet"
version = "0.0.1"

application {
    mainClass.set("com.example.panaqet.server.ApplicationKt")
}


dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.logback.classic)
    
    // Client for Paystack
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("com.h2database:h2:2.2.224")
    
    testImplementation(libs.junit)
}


