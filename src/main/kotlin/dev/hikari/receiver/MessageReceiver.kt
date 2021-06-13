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
                        is Voice -> "[voice:${msg.url}]"
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
            group.sendMessage("「${hitokoto.hitokoto}」 ———— ${hitokoto.from}")
        }

        startsWith("点歌") { keyword ->
            val qqMusicSearch = Api.searchQQMusic(keyword)
            val song = qqMusicSearch.data.song.list[0]
            val playUrl = Api.getQQMusicPlayUrl(song.songmid)
            group.sendMessage(
                MusicShare(
                    kind = MusicKind.QQMusic,
                    title = song.songname,
                    summary = song.singer[0].name,
                    jumpUrl = "https://i.y.qq.com/v8/playsong.html?hosteuin=7wCPoK6l7wcl&sharefrom=&from_id=7384606714&from_idtype=10015&from_name=(7rpl)&songid=${song.songid}&songmid=&type=0&platform=1&appsongtype=1&_wv=1&source=qq&appshare=iphone&media_mid=${song.media_mid}&ADTAG=qfshare&_wv=1",
                    pictureUrl = "https://y.qq.com/music/photo_new/T002R300x300M000${song.albummid}.jpg",
                    musicUrl = playUrl
                )
            )
        }

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
