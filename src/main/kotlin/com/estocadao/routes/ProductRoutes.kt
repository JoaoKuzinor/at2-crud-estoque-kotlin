package com.estocadao.routes

import com.estocadao.models.ProductRequest
import com.estocadao.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes() {
    val productService = ProductService()

    route("/products") {

        // GET /products - Listar todos os produtos
        get {
            val products = productService.getAllProducts()
            call.respond(HttpStatusCode.OK, products)
        }

        // GET /products/{id} - Buscar produto por ID
        get("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do produto é obrigatório")
            val product = productService.getProductById(id)
            call.respond(HttpStatusCode.OK, product)
        }

        // POST /products - Criar novo produto
        post {
            val request = call.receive<ProductRequest>()
            if (request.name.isBlank()) {
                throw IllegalArgumentException("O campo 'name' é obrigatório e não pode ser vazio")
            }
            if (request.sku.isBlank()) {
                throw IllegalArgumentException("O campo 'sku' é obrigatório e não pode ser vazio")
            }
            val product = productService.createProduct(request)
            call.respond(HttpStatusCode.Created, product)
        }

        // PUT /products/{id} - Atualizar produto
        put("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do produto é obrigatório")
            val request = call.receive<ProductRequest>()
            if (request.name.isBlank()) {
                throw IllegalArgumentException("O campo 'name' é obrigatório e não pode ser vazio")
            }
            if (request.sku.isBlank()) {
                throw IllegalArgumentException("O campo 'sku' é obrigatório e não pode ser vazio")
            }
            val product = productService.updateProduct(id, request)
            call.respond(HttpStatusCode.OK, product)
        }

        // DELETE /products/{id} - Remover produto
        delete("/{id}") {
            val id = call.parameters["id"]
                ?: throw IllegalArgumentException("ID do produto é obrigatório")
            productService.deleteProduct(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
