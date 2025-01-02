package dev.hikari.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
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
    val database: DatabaseConfig,
    val masterQQ: Long,
    @SerialName("alapi_token")
    val alapiToken: String,
    @SerialName("deep_seek_token")
    val deepSeekToken:String,
) {

    @Serializable
    data class DatabaseConfig(
        val url: String?,
        val driverClassName: String?,
        val username: String?,
        val password: String?
    )
}
