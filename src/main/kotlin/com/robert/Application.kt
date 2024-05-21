package com.robert

import com.robert.db.DatabaseFactory
import com.robert.db.dao.brand.BrandDaoImpl
import com.robert.db.dao.cart.CartDaoImpl
import com.robert.db.dao.category.CategoryDaoImpl
import com.robert.db.dao.order.OrderDaoImpl
import com.robert.db.dao.shoe.ShoeDaoImpl
import com.robert.db.dao.user.UserDaoImpl
import com.robert.plugins.configureMonitoring
import com.robert.plugins.configureRouting
import com.robert.plugins.configureSecurity
import com.robert.plugins.configureSerialization
import com.robert.repositories.cart.CartRepositoryImpl
import com.robert.repositories.shoe.BrandRepositoryImpl
import com.robert.repositories.shoe.CategoryRepositoryImpl
import com.robert.repositories.shoe.ShoeRepositoryImpl
import com.robert.repositories.user.UserRepositoryImpl
import com.robert.security.hashing.SHA256HashingService
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 7L * 1000L * 60L * 60L * 24L,
        secret = "my-secret"
    )
    val tokenService = JwtTokenService()
    val hashingService = SHA256HashingService()
    val categoryDao = CategoryDaoImpl()
    val brandDao = BrandDaoImpl()
    val shoeDao = ShoeDaoImpl()
    val cartDao = CartDaoImpl(shoeDao = shoeDao)
    val userDao = UserDaoImpl(shoeDao = shoeDao)
    val orderDao = OrderDaoImpl(shoeDao = shoeDao)
    val userRepository = UserRepositoryImpl(
        userDao = userDao,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        hashingService = hashingService
    )

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
        orderDao = orderDao
    )
}
