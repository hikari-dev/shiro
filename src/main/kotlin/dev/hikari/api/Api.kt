package dev.hikari.api

import dev.hikari.api.entity.Hitokoto
import dev.hikari.api.entity.QQMusicSearch
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object Api {

    private val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(8, TimeUnit.SECONDS)
                readTimeout(8, TimeUnit.SECONDS)
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

    suspend fun searchQQMusic(keyword: String): QQMusicSearch {
        val rspStr = httpClient.get<String>("https://c.y.qq.com/soso/fcgi-bin/music_search_new_platform?w=$keyword")
            .removePrefix("callback(")
            .removeSuffix(")")
        return json.decodeFromString(QQMusicSearch.serializer(), rspStr)
    }

    suspend fun getZuAnSentence(): String {
        return httpClient.get("https://zuanbot.com/api.php?lang=zh_cn") {
            header("Referer", "https://zuanbot.com/")
        }
    }

}
