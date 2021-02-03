package dev.hikari.receiver

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.hikari.api.Api
import dev.hikari.config.ShiroConfig
import dev.hikari.database.History
import dev.hikari.shiro
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat

/**
 * Handle all the messages shiro received.
 */
fun handleMessages() {

    handleFriendMessages()

    handleGroupMessages()

    if (checkDatabaseConfigValid()) {
        storeMessagesToDatabase()
        markRecalledMessages()
    }

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
        "一言" {
            val hitokoto = Api.getHitokoto()
            group.sendMessage("${hitokoto.hitokoto}\n来自于：${hitokoto.from}")
        }

        startsWith("点歌") {
            val songName = message.content.removePrefix("点歌").trim()
//            val qqMusicSearch = Api.searchQQMusic(cmd)
            val fake = SimpleServiceMessage(
                0,
                "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[分享] 花びらたちのマーチ\" sourceMsgId=\"0\" url=\"https://y.qq.com/n/yqq/song/001JMbsq4KHvmM.html\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"2\"><audio cover=\"http://y.gtimg.cn/music/photo_new/T002R300x300M000001UdhE42Q0wYz_1.jpg?max_age=2592000\" src=\"http://isure.stream.qqmusic.qq.com/M800003G0E3Q3p9YgX.mp3?guid=9146710&amp;vkey=B26F5D65ADDE629AB201E2F916AE1997A324EBBD13262C57B12A1715854E2C3AB8E52BDDE26E6B2CE0182F60E0EB47018A6E96B7B3C496AD&amp;uin=956581739&amp;fromtag=66\" /><title>花びらたちのマーチ</title><summary>Aimer</summary></item><source name=\"QQ音乐\" icon=\"https://i.gtimg.cn/open/app_icon/01/07/98/56/1101079856_100_m.png?date=20200503\" url=\"http://web.p.qq.com/qqmpmobile/aio/app.html?id=1101079856\" action=\"app\" a_actionData=\"com.tencent.qqmusic\" i_actionData=\"tencent1101079856://\" appid=\"1101079856\" /></msg>"
            )

        }
    }
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

fun checkDatabaseConfigValid(): Boolean {
    val dbConfig = ShiroConfig.config.database
    return !(dbConfig.username == null || dbConfig.password == null || dbConfig.driverClassName == null || dbConfig.url == null)
}
