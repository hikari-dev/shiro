package dev.hikari.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.Serializable
import java.io.File

object ShiroConfig {
    val config by lazy {
        val configStr = File("shiroConfig.yml").readText()
        Yaml(configuration = YamlConfiguration(strictMode = false)).decodeFromString(Config.serializer(), configStr)
    }
}

@Serializable
data class Config(
    val qqBot: QQBotConfig,
    val telegramBot: TelegramBotConfig,
    val database: DatabaseConfig,
    val proxy: Proxy,
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
        val token: String?,
        val qqGroup: Long?,
        val telegramGroup: Int?,
        val receiveInterval: Int?
    )

    @Serializable
    data class DatabaseConfig(
        val url: String?,
        val driverClassName: String?,
        val username: String?,
        val password: String?
    )

    @Serializable
    data class Proxy(
        val hostname: String?,
        val port: Int?
    )
}
