package dev.hikari.receiver

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.hikari.api.Api
import dev.hikari.config.ShiroConfig
import dev.hikari.database.History
import dev.hikari.shiro
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.FlashImage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.buildMessageChain
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat

/**
 * Handle all the messages shiro received.
 */
suspend fun handleMessages() {

    storeMessagesToDatabase()

    handleFriendMessages()

    handleGroupMessages()

    markRecalledMessages()

    receiveTelegramMessageUpdates()
}

private val db by lazy {
    val config = HikariConfig().apply {
        jdbcUrl = ShiroConfig.config.database.url
        driverClassName = ShiroConfig.config.database.driverClassName
        username = ShiroConfig.config.database.username
        password = ShiroConfig.config.database.password
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    val database = Database.connect(dataSource)
    transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.createMissingTablesAndColumns(History)
    }
    database
}

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private fun storeMessagesToDatabase() {
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
                it[recalled] = 0
            }
        }
    }
}

/**
 * Handle all friend message event.
 */
private fun handleFriendMessages() {

}

/**
 * Handle all group message event.
 */
private fun handleGroupMessages() {
    shiro.eventChannel.subscribeGroupMessages {
        always {
            if (group.id == ShiroConfig.config.telegramBot.qqGroup) {
                sendMessageToTelegramGroup(message.contentToString())
            }
        }
        "一言" {
            val hitokoto = Api.getHitokoto()
            group.sendMessage("${hitokoto.hitokoto}\n来自于：${hitokoto.from}")
        }
    }
}

private fun sendMessageToTelegramGroup(message: String) {

}

private fun markRecalledMessages() {
    shiro.eventChannel.subscribeAlways<MessageRecallEvent> { event ->
        if (event is MessageRecallEvent.FriendRecall) {
            transaction(db) {
                History.update({
                    (History.qq eq event.operatorId) and (History.serverId eq event.messageIds.joinToString())
                }) {
                    it[recalled] = 1
                }
            }
        } else if (event is MessageRecallEvent.GroupRecall) {
            transaction(db) {
                History.update({
                    (History.groupQQ eq event.group.id) and (History.serverId eq event.messageIds.joinToString())
                }) {
                    it[recalled] = 1
                }
            }
        }
    }
}

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun receiveTelegramMessageUpdates() = withContext(Dispatchers.IO + SupervisorJob()) {
    if (ShiroConfig.config.telegramBot.telegramGroup == 0) return@withContext
    val ticker = ticker(ShiroConfig.config.telegramBot.receiveInterval * 1000L, 0)
    for (item in ticker) {
        val updates = Api.getTelegramMessages(ShiroConfig.config.telegramBot.token)
        if (updates.isEmpty()) continue
        for (update in updates) {
            if (update.message?.chat?.id == ShiroConfig.config.telegramBot.telegramGroup) {
                shiro.getGroup(ShiroConfig.config.telegramBot.qqGroup)?.sendMessage(buildMessageChain {
                    +"${update.message.from?.firstName}${update.message.from?.lastName}："
                    +update.message.text
                })
            }
        }
    }
}
