
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val commons_codec_version: String by project
val postgresql_driver_version: String by project
val hikaricp_version: String by project
val ehcache_version: String by project


plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.robert"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
ktor {
    fatJar {
        archiveFileName.set("ndula.jar")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11)) // or 22, whichever you prefer
    }
}

// Set the target JVM version for Kotlin
kotlin {
    jvmToolchain(11) // or 22 to match the Java version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml:2.3.10")
    implementation("commons-codec:commons-codec:$commons_codec_version")
    implementation("org.postgresql:postgresql:$postgresql_driver_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("org.ehcache:ehcache:$ehcache_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("aws.sdk.kotlin:s3:1.0.0")

    // Google Maps services
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.25")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
