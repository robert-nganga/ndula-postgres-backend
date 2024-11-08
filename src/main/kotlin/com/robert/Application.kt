package com.robert

import com.google.maps.GeoApiContext
import com.robert.aws.S3ClientFactory
import com.robert.db.DatabaseFactory
import com.robert.db.dao.brand.BrandDaoImpl
import com.robert.db.dao.cart.CartDaoImpl
import com.robert.db.dao.category.CategoryDaoImpl
import com.robert.db.dao.order.OrderDaoImpl
import com.robert.db.dao.review.ReviewDaoImpl
import com.robert.db.dao.shoe.ShoeDaoImpl
import com.robert.db.dao.user.UserDaoImpl
import com.robert.db.dao.wish_list.WishListDaoImpl
import com.robert.location_services.LocationServiceImpl
import com.robert.plugins.configureMonitoring
import com.robert.plugins.configureRouting
import com.robert.plugins.configureSecurity
import com.robert.plugins.configureSerialization
import com.robert.repositories.cart.CartRepositoryImpl
import com.robert.repositories.images.ImageRepositoryImpl
import com.robert.repositories.order.OrderRepositoryImpl
import com.robert.repositories.reviews.ReviewRepositoryImpl
import com.robert.repositories.shoe.BrandRepositoryImpl
import com.robert.repositories.shoe.CategoryRepositoryImpl
import com.robert.repositories.shoe.ShoeRepositoryImpl
import com.robert.repositories.user.UserRepositoryImpl
import com.robert.repositories.wish_list.WishListRepositoryImpl
import com.robert.security.hashing.SHA256HashingService
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenConfig
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() = runBlocking {

    val geoContext = GeoApiContext.Builder()
        .apiKey(System.getenv("GOOGLE_API_KEY"))
        .build()

    environment.monitor.subscribe(ApplicationStopped){
        geoContext.shutdown()
    }

    DatabaseFactory.init()
    val s3Client = S3ClientFactory.init()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 7L * 4L * 60L * 60L * 24L,
        secret = System.getenv("SECRET")
    )
    val tokenService = JwtTokenService()
    val hashingService = SHA256HashingService()
    val categoryDao = CategoryDaoImpl()
    val wishListDao = WishListDaoImpl()
    val reviewDao = ReviewDaoImpl()
    val reviewRepository = ReviewRepositoryImpl(reviewDao)
    val shoeDao = ShoeDaoImpl(wishListDao = wishListDao)
    val brandDao = BrandDaoImpl(shoeDao = shoeDao)
    val cartDao = CartDaoImpl(shoeDao = shoeDao)
    val userDao = UserDaoImpl(shoeDao = shoeDao)
    val orderDao = OrderDaoImpl()
    val userRepository = UserRepositoryImpl(
        userDao = userDao,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        hashingService = hashingService
    )
    val imageRepository = ImageRepositoryImpl(s3Client = s3Client)
    val locationService = LocationServiceImpl(geoContext)

    configureSerialization()
    configureMonitoring()
    configureSecurity(
        tokenConfig = tokenConfig,
    )
    configureRouting(
        hashingService = hashingService,
        userRepository = userRepository,
        categoryRepository = CategoryRepositoryImpl(categoryDao = categoryDao),
        brandRepository = BrandRepositoryImpl(brandDao = brandDao),
        shoeRepository = ShoeRepositoryImpl(shoeDao = shoeDao),
        cartRepository = CartRepositoryImpl(cartDao = cartDao),
        orderRepository = OrderRepositoryImpl(orderDao = orderDao),
        imageRepository = imageRepository,
        wishListRepository = WishListRepositoryImpl(wishListDao),
        reviewRepository = reviewRepository,
        locationService = locationService
    )
}
