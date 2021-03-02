import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "dev.hikari"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
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

    implementation("net.mamoe", "mirai-core", "2.4.1")

    implementation("com.charleskorn.kaml:kaml:0.26.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    val exposedVersion = "0.29.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("mysql:mysql-connector-java:8.0.22")
    implementation("com.zaxxer", "HikariCP", "4.0.2")

}
