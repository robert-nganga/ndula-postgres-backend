package com.robert.plugins

import com.robert.db.dao.order.OrderDao
import com.robert.db.dao.shoe.ShoeDao
import com.robert.repositories.cart.CartRepository
import com.robert.repositories.shoe.BrandRepository
import com.robert.repositories.shoe.CategoryRepository
import com.robert.repositories.shoe.ShoeRepository
import com.robert.repositories.user.UserRepository
import com.robert.routes.*
import com.robert.security.hashing.HashingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    userRepository: UserRepository,
    categoryRepository: CategoryRepository,
    brandRepository: BrandRepository,
    shoeRepository: ShoeRepository,
    cartRepository: CartRepository,
    orderDao: OrderDao
) {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "Healthy"))
        }
        authenticate {
            authCheck()
        }
        route("/auth") {
            login(
                userRepository = userRepository,
            )
            signUp(
                hashingService = hashingService,
                userRepository = userRepository
            )
        }

        authenticate {
            route("/categories") {
                categoryRoutes(categoryRepository)
            }
            route("/brands") {
                brandRoutes(brandRepository)
            }
            route("/shoes") {
                shoeRoutes(shoeRepository)
            }
            route("/cart") {
                cartRoutes(cartRepository)
            }
            route("/orders") {
                orderRoutes(orderDao)
            }
        }
    }
}
