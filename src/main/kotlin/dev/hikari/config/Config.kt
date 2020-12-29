package dev.hikari.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(val bot: BotConfig, val testGroup: Long) {

    @Serializable
    data class BotConfig(val qq: Long, val password: String)
}
