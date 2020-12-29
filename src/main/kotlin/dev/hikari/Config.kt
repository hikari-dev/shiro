package dev.hikari

import kotlinx.serialization.Serializable

@Serializable
data class Config(val bot: BotConfig) {

    @Serializable
    data class BotConfig(val qq: Long, val password: String)
}
