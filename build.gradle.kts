val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val postgres_version: String by project
val h2_version: String by project
plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

group = "encryptix"
version = "0.0.1"

application {
    mainClass.set("com.encryptix.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
tasks.compileTestJava {
    targetCompatibility = "17"
    sourceCompatibility = "17"
}

// Set Java version for Kotlin compilation tasks
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.0")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.40.1")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.pgpainless:pgpainless-core:1.6.2")
    implementation("org.bouncycastle:bcprov-jdk18on:1.72")
    implementation("org.bouncycastle:bcpg-jdk18on:1.72.2")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}
