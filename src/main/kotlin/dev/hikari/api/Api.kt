package dev.hikari.api

import dev.hikari.api.entity.Hitokoto
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json


object Api {

    private val httpClient = HttpClient(OkHttp)
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getHitokoto(): Hitokoto {
        val rspStr = httpClient.get<String>("https://v1.hitokoto.cn")
        return json.decodeFromString(Hitokoto.serializer(), rspStr)
    }

    suspend fun getWeather() {

    }
}
