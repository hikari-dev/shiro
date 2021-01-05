package dev.hikari.receiver

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.hikari.api.Api
import dev.hikari.config
import dev.hikari.database.History
import dev.hikari.shiro
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.FlashImage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat

/**
 * Handle all the messages shiro received.
 */
fun handleMessages() {

    storeMessagesToDatabase()

    handleFriendMessages()

    handleGroupMessages()
}

private val db by lazy {
    val config = HikariConfig().apply {
        jdbcUrl = config.database.url
        driverClassName = config.database.driverClassName
        username = config.database.username
        password = config.database.password
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    val database = Database.connect(dataSource)
    transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(History)
    }
    database
}

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun storeMessagesToDatabase() {
    shiro.eventChannel.subscribeAlways<MessageEvent> { event ->
        val messageContent = buildString {
            for (msg in event.message) {
                append(
                    when (msg) {
                        is Image -> "[image:${msg.queryUrl()}]"
                        is FlashImage -> "[flash:${msg.image.queryUrl()}]"
                        is At -> "[at:${msg.target}]"
                        else -> msg.contentToString()
                    }
                )
            }
        }
        transaction(db) {
            History.insert {
                it[serverId] = event.source.ids.joinToString()
                it[qq] = event.sender.id
                it[time] = dateFormatter.format(event.time.toLong() * 1000)
                if (event is GroupMessageEvent) {
                    it[groupQQ] = event.group.id
                }
                it[nick] = event.sender.nick
                it[nameCard] = event.sender.nameCardOrNick
                it[content] = messageContent
            }
        }
    }
}

/**
 * Handle all friend message event.
 */
fun handleFriendMessages() {

}

/**
 * Handle all group message event.
 */
fun handleGroupMessages() {
    shiro.eventChannel.subscribeGroupMessages {
        "一言" {
            val hitokoto = Api.getHitokoto()
            group.sendMessage("${hitokoto.hitokoto}\n来自于:${hitokoto.from}")
        }
    }
}
