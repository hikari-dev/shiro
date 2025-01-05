package dev.hikari.api

import dev.hikari.api.entity.*
import dev.hikari.config.ShiroConfig
import dev.hikari.shiro
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.util.*
import java.util.concurrent.TimeUnit

object Api {

    private val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(8, TimeUnit.SECONDS)
                readTimeout(8, TimeUnit.SECONDS)
            }
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    shiro.logger.debug(message)
                }
            }
            level = LogLevel.BODY
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val chatHistory = LinkedList<Prompt>().also { it.add(Prompt("system", "You are a helpful assistant.")) }

    suspend fun getHitokoto(): Hitokoto {
        val rspStr = httpClient.get("https://v1.hitokoto.cn").body<String>()
        return json.decodeFromString(Hitokoto.serializer(), rspStr)
    }

    suspend fun searchQQMusic(keyword: String): QQMusicSearch {
        val rspStr =
            httpClient.get("https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?format=json&platform=yqq.json&key=${keyword}")
                .body<String>()
        return json.decodeFromString(QQMusicSearch.serializer(), rspStr)
    }

    suspend fun getQQMusicPlayUrl(mid: String): String {
        val rspStr =
            httpClient.get("https://u.y.qq.com/cgi-bin/musicu.fcg?data={\"req\": {\"module\": \"CDN.SrfCdnDispatchServer\", \"method\": \"GetCdnDispatch\", \"param\": {\"guid\": \"3982823384\", \"calltype\": 0, \"userip\": \"\"}}, \"req_0\": {\"module\": \"vkey.GetVkeyServer\", \"method\": \"CgiGetVkey\", \"param\": {\"guid\": \"3982823384\", \"songmid\": [\"$mid\"], \"songtype\": [0], \"uin\": \"0\", \"loginflag\": 1, \"platform\": \"20\"}}, \"comm\": {\"uin\": 0, \"format\": \"json\", \"ct\": 24, \"cv\": 0}}")
                .body<String>()
        val qqMusicPlay = json.decodeFromString(QQMusicPlay.serializer(), rspStr)
        return "https://isure.stream.qqmusic.qq.com/${qqMusicPlay.req_0.data.midurlinfo[0].purl}"
    }

    suspend fun getZuAnSentence(): String {
        return httpClient.get("https://zuanbot.com/api.php?lang=zh_cn") {
            header("Referer", "https://zuanbot.com/")
        }.body<String>()
    }

    suspend fun getBilibiliDynamic() {
        //https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=1111111111&offset_dynamic_id=0&need_top=0&host_uid=
        return
    }

    suspend fun getChuckNorrisFacts(): ChuckNorrisFact {
        val repStr = httpClient.get("https://api.chucknorris.io/jokes/random?category=dev").body<String>()
        return json.decodeFromString(ChuckNorrisFact.serializer(), repStr)
    }

    suspend fun getDailyNews(): DailyNews {
        val repStr =
            httpClient.get("https://v2.alapi.cn/api/zaobao?format=json&token=${ShiroConfig.config.alapiToken}")
                .body<String>()
        val resp = json.decodeFromString(DailyNewsResp.serializer(), repStr)
        return resp.data
    }

    suspend fun getDailyNewsPicture(url: String): ExternalResource {
        val requestBuilder = HttpRequestBuilder().apply {
            url(url)
        }
        return HttpStatement(requestBuilder, httpClient)
            .execute()
            .bodyAsChannel()
            .toInputStream()
            .use {
                it.toExternalResource()
            }
    }

    suspend fun deepSeekChat(content: String): String {
        addChatHistory(Prompt("user", content))
        val respStr = httpClient.post("https://api.deepseek.com/chat/completions") {
            header("Authorization", "Bearer ${ShiroConfig.config.deepSeekToken}")
            header("Content-Type", "application/json")
            setBody(json.encodeToString(DeepSeekChat(messages = chatHistory)))
        }.body<String>()
        val resp = json.decodeFromString<ChatResponse>(respStr)
        val promptResp = resp.choices[0].message
        addChatHistory(promptResp)
        return promptResp.content
    }

    private fun addChatHistory(prompt: Prompt) {
        if (chatHistory.size >= 21) {
            chatHistory.removeAt(1)
        }
        chatHistory.add(prompt)
    }
}
