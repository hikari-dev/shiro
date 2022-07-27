package dev.hikari.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyNewsResp(
    val code: Int,
    val msg: String,
    val data: DailyNews,
    val time: Long,
    @SerialName("log_id")
    val logId: Long
)

@Serializable
data class DailyNews(
    val date: String,
    val news: List<String>,
    val weiyu: String,
    val image: String,
)
