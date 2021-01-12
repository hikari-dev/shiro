package dev.hikari.api

import dev.hikari.api.entity.Hitokoto
import dev.hikari.api.entity.TelegramRsp
import dev.hikari.config.ShiroConfig
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


object Api {

    private val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(8, TimeUnit.SECONDS)
                readTimeout(8, TimeUnit.SECONDS)

                val proxyHostName = ShiroConfig.config.proxy.hostname
                val proxyPort = ShiroConfig.config.proxy.port
                if (proxyHostName != null && proxyPort != null) {
                    proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxyHostName, proxyPort)))
                }
            }
        }
    }
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private var offset = 0

    suspend fun getHitokoto(): Hitokoto {
        val rspStr = httpClient.get<String>("https://v1.hitokoto.cn")
        return json.decodeFromString(Hitokoto.serializer(), rspStr)
    }

    //https://api.telegram.org/bot123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11/getMe
    suspend fun getTelegramMessage(): List<TelegramRsp.Update> {
        val rspStr =
            httpClient.get<String>("https://api.telegram.org/bot${ShiroConfig.config.telegramBot.token}/getUpdates?offset=${if (offset > 0) offset else ""}")
        val rsp = json.decodeFromString(TelegramRsp.serializer(), rspStr)
        if (rsp.result.isNullOrEmpty()) return emptyList()
        offset = rsp.result.last().updateId + 1
        return rsp.result
    }

    suspend fun sendTelegramMessage(message: String) {
        httpClient.get<String>("https://api.telegram.org/bot${ShiroConfig.config.telegramBot.token}/sendMessage?chat_id=${ShiroConfig.config.telegramBot.telegramGroup}&text=$message")
    }
}
