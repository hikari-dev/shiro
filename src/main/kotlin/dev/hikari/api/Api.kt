package dev.hikari.api

import dev.hikari.api.entity.Hitokoto
import dev.hikari.api.entity.TelegramRsp
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

    //https://api.telegram.org/bot123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11/getMe
    suspend fun getTelegramMessages(token: String): TelegramRsp {
        val rspStr = httpClient.get<String>("https://api.telegram.org/bot$token/getMe")
        return json.decodeFromString(TelegramRsp.serializer(), rspStr)
    }
}
