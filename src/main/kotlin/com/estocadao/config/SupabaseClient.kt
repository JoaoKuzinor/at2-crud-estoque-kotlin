package com.estocadao.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SupabaseConfig {
    val url: String = "https://cugrwgritgnmienipfbv.supabase.co"

    val apiKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN1Z3J3Z3JpdGdubWllbmlwZmJ2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzkwNDUzMDMsImV4cCI6MjA5NDYyMTMwM30.Ly98yVQqC7E6zmTNl55B5g63dIbzOuzUyuNsFOb_T-I"

    val restUrl: String = "$url/rest/v1"
}

val supabaseClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}
