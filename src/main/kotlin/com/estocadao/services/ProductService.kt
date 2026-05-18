package com.estocadao.services

import com.estocadao.config.SupabaseConfig
import com.estocadao.config.supabaseClient
import com.estocadao.models.Product
import com.estocadao.models.ProductRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ProductService {

    private val baseUrl = "${SupabaseConfig.restUrl}/products"

    private fun HttpRequestBuilder.supabaseHeaders() {
        header("apikey", SupabaseConfig.apiKey)
        header("Authorization", "Bearer ${SupabaseConfig.apiKey}")
        header("Content-Type", "application/json")
    }

    suspend fun getAllProducts(): List<Product> {
        val response: HttpResponse = supabaseClient.get(baseUrl) {
            supabaseHeaders()
            header("Prefer", "return=representation")
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao buscar produtos: ${response.status}")
        }
        return response.body()
    }

    suspend fun getProductById(id: String): Product {
        val response: HttpResponse = supabaseClient.get("$baseUrl?id=eq.$id") {
            supabaseHeaders()
            header("Accept", "application/vnd.pgrst.object+json")
        }
        if (response.status == HttpStatusCode.NotFound || response.status.value == 406) {
            throw NoSuchElementException("Produto com id '$id' não encontrado")
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao buscar produto: ${response.status}")
        }
        return response.body()
    }

    suspend fun createProduct(request: ProductRequest): Product {
        val response: HttpResponse = supabaseClient.post(baseUrl) {
            supabaseHeaders()
            header("Prefer", "return=representation")
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw IllegalArgumentException("Erro ao criar produto: $body")
        }
        val products: List<Product> = response.body()
        return products.firstOrNull()
            ?: throw Exception("Produto criado mas não retornado")
    }

    suspend fun updateProduct(id: String, request: ProductRequest): Product {
        // Verifica se existe antes de atualizar
        getProductById(id)

        val response: HttpResponse = supabaseClient.patch("$baseUrl?id=eq.$id") {
            supabaseHeaders()
            header("Prefer", "return=representation")
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw IllegalArgumentException("Erro ao atualizar produto: $body")
        }
        val products: List<Product> = response.body()
        return products.firstOrNull()
            ?: throw NoSuchElementException("Produto com id '$id' não encontrado após atualização")
    }

    suspend fun deleteProduct(id: String) {
        // Verifica se existe antes de deletar
        getProductById(id)

        val response: HttpResponse = supabaseClient.delete("$baseUrl?id=eq.$id") {
            supabaseHeaders()
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao deletar produto: ${response.status}")
        }
    }
}
