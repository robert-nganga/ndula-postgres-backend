package com.robert.plugins

import com.robert.location_services.LocationService
import com.robert.repositories.cart.CartRepository
import com.robert.repositories.images.ImageRepository
import com.robert.repositories.order.OrderRepository
import com.robert.repositories.reviews.ReviewRepository
import com.robert.repositories.shoe.BrandRepository
import com.robert.repositories.shoe.CategoryRepository
import com.robert.repositories.shoe.ShoeRepository
import com.robert.repositories.user.UserRepository
import com.robert.repositories.wish_list.WishListRepository
import com.robert.repositories.wish_list.WishListRepositoryImpl
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
    orderRepository: OrderRepository,
    imageRepository: ImageRepository,
    wishListRepository: WishListRepository,
    reviewRepository: ReviewRepository,
    locationService: LocationService
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

        route("/location"){
            locationRoutes(locationService)
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
                orderRoutes(orderRepository)
            }
            route("/images") {
                imageRoutes(imageRepository)
            }
            route("/wishlist"){
                wishlistRoutes(wishListRepository)
            }
            route("/reviews"){
                reviewRoutes(reviewRepository)
            }
        }
    }
}
