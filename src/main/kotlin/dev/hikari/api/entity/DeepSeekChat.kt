package dev.hikari.api.entity

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekChat(
    val model: String = "deepseek-reasoner",
    val stream: Boolean = false,
    val messages: List<Prompt>
)


@Serializable
data class Prompt(
    val role: String,
    val content: String,
)

@Serializable
data class ChatResponse(
    val id: String,
    val model: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val index: Int,
    val message: Prompt,
)