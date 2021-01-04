package dev.hikari.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val bot: BotConfig,
    val database: DatabaseConfig,
    val testGroup: Long,
    val masterQQ: Long
) {

    @Serializable
    data class BotConfig(
        val qq: Long,
        val password: String
    )

    @Serializable
    data class DatabaseConfig(
        val url: String,
        val driverClassName: String,
        val username: String,
        val password: String
    )
}
