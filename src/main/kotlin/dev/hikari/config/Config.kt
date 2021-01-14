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
        val url: String?,
        val driverClassName: String?,
        val username: String?,
        val password: String?
    )
}
