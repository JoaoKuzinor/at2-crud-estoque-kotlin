package com.estocadao.routes

import com.estocadao.models.StockItemRequest
import com.estocadao.services.StockService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stockRoutes() {
    val stockService = StockService()

    route("/stock") {

        // GET /stock/summary - DEVE vir antes de /{id} para não ser interceptado
        get("/summary") {
            val summary = stockService.getStockSummary()
            call.respond(HttpStatusCode.OK, summary)
        }

        // GET /stock - Listar todos os itens de estoque
        get {
            val items = stockService.getAllStockItems()
            call.respond(HttpStatusCode.OK, items)
        }

        // GET /stock/{id} - Buscar item por ID
        get("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do item de estoque é obrigatório")
            val item = stockService.getStockItemById(id)
            call.respond(HttpStatusCode.OK, item)
        }

        // POST /stock - Adicionar item ao estoque
        post {
            val request = call.receive<StockItemRequest>()
            if (request.productId.isBlank()) {
                throw IllegalArgumentException("O campo 'product_id' é obrigatório")
            }
            val item = stockService.createStockItem(request)
            call.respond(HttpStatusCode.Created, item)
        }

        // PUT /stock/{id} - Atualizar item do estoque
        put("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do item de estoque é obrigatório")
            val request = call.receive<StockItemRequest>()
            if (request.productId.isBlank()) {
                throw IllegalArgumentException("O campo 'product_id' é obrigatório")
            }
            val item = stockService.updateStockItem(id, request)
            call.respond(HttpStatusCode.OK, item)
        }

        // DELETE /stock/{id} - Remover item do estoque
        delete("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do item de estoque é obrigatório")
            stockService.deleteStockItem(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
