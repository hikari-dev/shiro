import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.*

plugins {
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("plugin.atomicfu") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.hikari"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("top.mrxiaom:overflow-core-all:2.16.0-d7b9a37-SNAPSHOT")
    implementation("net.mamoe", "mirai-core-api", "2.16.0")

    implementation("com.charleskorn.kaml:kaml:0.60.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    val exposedVersion = "0.52.0"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")

    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    implementation(kotlin("test"))
    implementation(kotlin("test-junit5"))
    implementation(kotlin("test-common"))
    implementation(kotlin("test-annotations-common"))
}
