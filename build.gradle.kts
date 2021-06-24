import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.hikari"
version = "0.0.1"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.mamoe", "mirai-core", "2.6.7")

    implementation("com.charleskorn.kaml:kaml:0.34.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    implementation(platform("org.jetbrains.exposed:exposed-bom:0.32.1"))
    implementation("org.jetbrains.exposed", "exposed-core")
    implementation("org.jetbrains.exposed", "exposed-dao")
    implementation("org.jetbrains.exposed", "exposed-jdbc")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("com.zaxxer", "HikariCP", "4.0.2")

    implementation(kotlin("test"))
    implementation(kotlin("test-junit5"))
    implementation(kotlin("test-common"))
    implementation(kotlin("test-annotations-common"))


}
