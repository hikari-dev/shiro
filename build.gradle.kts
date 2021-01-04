import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.useIR = true
}

tasks.withType<Jar> {
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
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.mamoe:mirai-core:2.0-M2-2")

    implementation("com.charleskorn.kaml:kaml:0.26.0")

    implementation("org.quartz-scheduler:quartz:2.3.2")

    val exposedVersion = "0.28.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("com.zaxxer", "HikariCP", "3.4.5")

}
