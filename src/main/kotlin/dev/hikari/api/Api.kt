package dev.hikari.api

import dev.hikari.api.entity.Hitokoto
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
                val type = ShiroConfig.config.proxy.type
                if (proxyHostName != null && proxyPort != null && type != null) {
                    when (type) {
                        0 -> {
                            proxy(Proxy(Proxy.Type.DIRECT, InetSocketAddress(proxyHostName, proxyPort)))
                        }
                        1 -> {
                            proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHostName, proxyPort)))
                        }
                        2 -> {
                            proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxyHostName, proxyPort)))
                        }
                    }
                }
            }
        }
    }
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getHitokoto(): Hitokoto {
        val rspStr = httpClient.get<String>("https://v1.hitokoto.cn")
        return json.decodeFromString(Hitokoto.serializer(), rspStr)
    }

}
