import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0" // Kotlin language support
    id("io.ktor.plugin") version "2.3.0" // Ktor framework plugin
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" // Kotlin serialization plugin
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.shortener.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server core and Netty engine for running the web server
    implementation("io.ktor:ktor-server-core-jvm:2.3.0") // Core Ktor server
    implementation("io.ktor:ktor-server-netty-jvm:2.3.0") // Netty engine
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.0") // Content negotiation plugin

    // Ktor serialization for JSON handling
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0") // JSON serialization

    // Exposed ORM for database operations
    implementation("org.jetbrains.exposed:exposed-core:0.45.0") // Exposed core
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0") // Exposed DAO
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0") // Exposed JDBC
    implementation("org.jetbrains.exposed:exposed-java-time:0.45.0") // Exposed Java Time support

    // H2 database for development (in-memory or file-based)
    implementation("com.h2database:h2:2.2.224")

    // Logback for logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Ktor config and call logging
    implementation("io.ktor:ktor-server-config-yaml:2.3.0")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.0")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    // For kotlinx.datetime if you want to use @Contextual for Instant serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
}

// Dependency explanations:
// - Ktor: Web framework for building asynchronous servers in Kotlin
// - Exposed: ORM library for type-safe database access
// - H2: Lightweight, in-memory database for development/testing
// - Serialization: For handling JSON (request/response bodies)
// - Logback: Logging framework for JVM applications

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

kotlin {
    jvmToolchain(21)
}