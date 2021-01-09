package dev.hikari.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val qqBot: QQBotConfig,
    val telegramBot: TelegramBotConfig,
    val database: DatabaseConfig,
    val testGroup: Long,
    val masterQQ: Long
) {

    @Serializable
    data class QQBotConfig(
        val qq: Long,
        val password: String
    )

    @Serializable
    data class TelegramBotConfig(
        val token: String
    )

    @Serializable
    data class DatabaseConfig(
        val url: String,
        val driverClassName: String,
        val username: String,
        val password: String
    )
}
