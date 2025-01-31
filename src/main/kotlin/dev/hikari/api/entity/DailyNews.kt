package dev.hikari.api.entity

import kotlinx.serialization.Serializable

@Serializable
data class DailyNewsResp(
    val success: Boolean,
    val message: String,
    val code: Int,
    val data: DailyNews,
    val time: Long,
)

@Serializable
data class DailyNews(
    val date: String,
    val news: List<String>,
    val weiyu: String,
    val image: String,
)
