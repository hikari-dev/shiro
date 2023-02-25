import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.*

plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.hikari"
version = "0.0.1"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "dev.hikari.ShiroKt"
            )
        )
    }
    from("./") {
        include("build.gradle.kts")
    }
    archiveClassifier.set(SimpleDateFormat("yyyyMMddHHmmss").format(Date()))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.mamoe", "mirai-core", "2.14.0")

    implementation("com.charleskorn.kaml:kaml:0.52.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    val exposedVersion = "0.41.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
//    implementation("mysql:mysql-connector-java:8.0.32")
//    implementation("com.zaxxer", "HikariCP", "5.0.1")
    implementation("org.xerial:sqlite-jdbc:3.41.0.0")

    val ktorVersion = "2.2.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    val kumoVersion = "1.28"
    implementation("com.kennycason:kumo-core:$kumoVersion")
    implementation("com.kennycason:kumo-tokenizers:$kumoVersion")

    implementation(kotlin("test"))
    implementation(kotlin("test-junit5"))
    implementation(kotlin("test-common"))
    implementation(kotlin("test-annotations-common"))
}
