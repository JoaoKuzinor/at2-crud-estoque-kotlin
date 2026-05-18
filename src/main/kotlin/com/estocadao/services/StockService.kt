package com.estocadao.services

import com.estocadao.config.SupabaseConfig
import com.estocadao.config.supabaseClient
import com.estocadao.models.StockItem
import com.estocadao.models.StockItemRequest
import com.estocadao.models.StockSummary
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

// Resposta intermediária do Supabase para o summary (JOIN)
@Serializable
private data class StockWithProduct(
    val product_id: String,
    val quantity: Int,
    val products: ProductName? = null
)

@Serializable
private data class ProductName(
    val name: String
)

class StockService {

    private val baseUrl = "${SupabaseConfig.restUrl}/stock_items"

    private fun HttpRequestBuilder.supabaseHeaders() {
        header("apikey", SupabaseConfig.apiKey)
        header("Authorization", "Bearer ${SupabaseConfig.apiKey}")
        header("Content-Type", "application/json")
    }

    suspend fun getAllStockItems(): List<StockItem> {
        val response: HttpResponse = supabaseClient.get(baseUrl) {
            supabaseHeaders()
            header("Prefer", "return=representation")
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao buscar itens de estoque: ${response.status}")
        }
        return response.body()
    }

    suspend fun getStockItemById(id: String): StockItem {
        val response: HttpResponse = supabaseClient.get("$baseUrl?id=eq.$id") {
            supabaseHeaders()
            header("Accept", "application/vnd.pgrst.object+json")
        }
        if (response.status == HttpStatusCode.NotFound || response.status.value == 406) {
            throw NoSuchElementException("Item de estoque com id '$id' não encontrado")
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao buscar item de estoque: ${response.status}")
        }
        return response.body()
    }

    suspend fun createStockItem(request: StockItemRequest): StockItem {
        if (request.quantity < 0) {
            throw IllegalArgumentException("Quantidade não pode ser negativa")
        }
        if (request.unitPrice < 0) {
            throw IllegalArgumentException("Preço unitário não pode ser negativo")
        }

        val response: HttpResponse = supabaseClient.post(baseUrl) {
            supabaseHeaders()
            header("Prefer", "return=representation")
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw IllegalArgumentException("Erro ao criar item de estoque: $body")
        }
        val items: List<StockItem> = response.body()
        return items.firstOrNull()
            ?: throw Exception("Item criado mas não retornado")
    }

    suspend fun updateStockItem(id: String, request: StockItemRequest): StockItem {
        getStockItemById(id)

        if (request.quantity < 0) {
            throw IllegalArgumentException("Quantidade não pode ser negativa")
        }
        if (request.unitPrice < 0) {
            throw IllegalArgumentException("Preço unitário não pode ser negativo")
        }

        val response: HttpResponse = supabaseClient.patch("$baseUrl?id=eq.$id") {
            supabaseHeaders()
            header("Prefer", "return=representation")
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw IllegalArgumentException("Erro ao atualizar item de estoque: $body")
        }
        val items: List<StockItem> = response.body()
        return items.firstOrNull()
            ?: throw NoSuchElementException("Item de estoque com id '$id' não encontrado após atualização")
    }

    suspend fun deleteStockItem(id: String) {
        getStockItemById(id)

        val response: HttpResponse = supabaseClient.delete("$baseUrl?id=eq.$id") {
            supabaseHeaders()
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao deletar item de estoque: ${response.status}")
        }
    }

    suspend fun getStockSummary(): List<StockSummary> {
        val response: HttpResponse = supabaseClient.get("$baseUrl?select=product_id,quantity,products(name)") {
            supabaseHeaders()
        }
        if (!response.status.isSuccess()) {
            throw Exception("Erro ao buscar resumo do estoque: ${response.status}")
        }

        val items: List<StockWithProduct> = response.body()

        return items
            .groupBy { it.product_id }
            .map { (productId, stockItems) ->
                StockSummary(
                    productId = productId,
                    productName = stockItems.firstOrNull()?.products?.name ?: "Produto desconhecido",
                    totalQuantity = stockItems.sumOf { it.quantity }
                )
            }
    }
}