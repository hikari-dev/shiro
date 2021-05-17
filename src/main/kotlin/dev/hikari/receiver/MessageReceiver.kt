package dev.hikari.receiver

import dev.hikari.api.Api
import dev.hikari.command.ZuAnCommand
import dev.hikari.config.ShiroConfig
import dev.hikari.database.DbSettings
import dev.hikari.database.History
import dev.hikari.database.database
import dev.hikari.database.execAndMap
import dev.hikari.shiro
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.text.SimpleDateFormat
import java.util.*

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

/**
 * Handle all the messages shiro received.
 */
fun handleMessages() {

    handleFriendMessages()

    handleGroupMessages()

    if (DbSettings.configValid) {
        storeMessagesToDatabase()
        markRecalledMessages()
    }

}

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
        transaction(database) {
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

//        startsWith("点歌") { keyword ->
//            val qqMusicSearch = Api.searchQQMusic(keyword)
//            val fake = SimpleServiceMessage(
//                0,
//                "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[分享] 花びらたちのマーチ\" sourceMsgId=\"0\" url=\"https://y.qq.com/n/yqq/song/001JMbsq4KHvmM.html\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"2\"><audio cover=\"http://y.gtimg.cn/music/photo_new/T002R300x300M000001UdhE42Q0wYz_1.jpg?max_age=2592000\" src=\"http://isure.stream.qqmusic.qq.com/M800003G0E3Q3p9YgX.mp3?guid=9146710&amp;vkey=B26F5D65ADDE629AB201E2F916AE1997A324EBBD13262C57B12A1715854E2C3AB8E52BDDE26E6B2CE0182F60E0EB47018A6E96B7B3C496AD&amp;uin=956581739&amp;fromtag=66\" /><title>花びらたちのマーチ</title><summary>Aimer</summary></item><source name=\"QQ音乐\" icon=\"https://i.gtimg.cn/open/app_icon/01/07/98/56/1101079856_100_m.png?date=20200503\" url=\"http://web.p.qq.com/qqmpmobile/aio/app.html?id=1101079856\" action=\"app\" a_actionData=\"com.tencent.qqmusic\" i_actionData=\"tencent1101079856://\" appid=\"1101079856\" /></msg>"
//            )
//
//        }

//        always {
//
//        }

        //祖安宝典
        startsWith("boom") {
            val raw = message.contentToString()
            ZuAnCommand.parse(raw.split(" ").drop(1), group.id)
        }

        //查询聊天记录
        (sentBy(ShiroConfig.config.masterQQ) and startsWith("sudo")) {
            val sql = message.content.removePrefix("sudo").trim()
            var results: List<String>? = null
            transaction(database) {
                results = sql.execAndMap { resultSet ->
                    resultSet.getString("content")
                }
            }
            if (!results.isNullOrEmpty()) {
                group.sendMessage(buildMessageChain {
                    add("查询结果：")
                    for (result in results!!) {
                        add("\r\n" + result)
                    }
                })
            }
        }

        startsWith("百度") { query ->
            group.sendMessage("https://lmbtfy.tk/?q=${Base64.getEncoder().encodeToString(query.toByteArray())}")
        }

    }
}

private fun markRecalledMessages() {
    shiro.eventChannel.subscribeAlways<MessageRecallEvent> { event ->
        if (event is MessageRecallEvent.FriendRecall) {
            transaction(database) {
                History.update({
                    (History.qq eq event.operatorId) and (History.serverId eq event.messageIds.joinToString())
                }) {
                    it[recalled] = 1
                }
            }
        } else if (event is MessageRecallEvent.GroupRecall) {
            transaction(database) {
                History.update({
                    (History.groupQQ eq event.group.id) and (History.serverId eq event.messageIds.joinToString())
                }) {
                    it[recalled] = 1
                }
            }
        }
    }
}
