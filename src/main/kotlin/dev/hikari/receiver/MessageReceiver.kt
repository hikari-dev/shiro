package dev.hikari.receiver

import dev.hikari.api.Api
import dev.hikari.command.ZuAnCommand
import dev.hikari.config.ShiroConfig
import dev.hikari.database.History
import dev.hikari.database.database
import dev.hikari.database.execAndMap
import dev.hikari.shiro
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.*
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

    //save message history and mark recalled message
    storeMessagesToDatabase()
    markRecalledMessages()
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
                        is OnlineAudio -> "[audio:${msg.urlForDownload}]"
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
            group.sendMessage("「${hitokoto.hitokoto}」—— ${hitokoto.from}")
        }

        startsWith("点歌") { keyword ->
            val qqMusicSearch = Api.searchQQMusic(keyword)
            if (qqMusicSearch.data.song.count <= 0) {
                return@startsWith
            }
            val song = qqMusicSearch.data.song.itemList[0]
            val playUrl = Api.getQQMusicPlayUrl(song.mid)
            val picUrl = if (qqMusicSearch.data.album.count <= 0) {
                ""
            } else {
                qqMusicSearch.data.album.itemList[0].pic
            }
            group.sendMessage(
                MusicShare(
                    kind = MusicKind.QQMusic,
                    title = song.name,
                    summary = song.singer,
                    jumpUrl = "https://i.y.qq.com/v8/playsong.html?hosteuin=7wCPoK6l7wcl&sharefrom=&from_id=7384606714&from_idtype=10015&from_name=(7rpl)&songid=${song.id}&songmid=&type=0&platform=1&appsongtype=1&_wv=1&source=qq&appshare=iphone&media_mid=${song.mid}&ADTAG=qfshare&_wv=1",
                    pictureUrl = picUrl,
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

        startsWith("Praise") { name ->
            if (name.isEmpty()) {
                group.sendMessage("你倒是说你要Praise谁呀 Baka!")
                return@startsWith
            }
            val fact = Api.getChuckNorrisFacts().value
            if (fact.contains("Chuck Norris", true)) {
                val replace = fact.replace("Chuck Norris", name, true)
                group.sendMessage(replace)
            } else if (fact.contains("Chuck", true)) {
                val replace = fact.replace("Chuck", name, true)
                group.sendMessage(replace)
            } else {
                group.sendMessage("哭咩纳塞，Praise失败！")
            }
        }

        "早报来" {
            val dailyNews = Api.getDailyNews()
            val picture = Api.getDailyNewsPicture(dailyNews.image)
            group.sendImage(picture)
        }

//        "看看我都说了啥" {
//            var histories: List<String>? = null
//            transaction(database) {
//                histories = History.select { (History.qq eq sender.id) and (History.groupQQ eq group.id) }
//                    .map { it[History.content] }
//                    .filter { !it.startsWith("[") && !it.contains("at:") }
//                    .toList()
//            }
//            if (histories.isNullOrEmpty()) {
//                group.sendMessage("没查到你说过啥捏")
//            }
//            val bytes = WordCloudUtils.generateWordCloud(histories!!)
//            group.sendImage(bytes.toExternalResource("png"))
//
//        }
    }

    shiro.eventChannel.subscribeAlways<MemberJoinEvent> {
        group.sendMessage(buildMessageChain {
            add("欢迎新人")
            add(member.at())
            add("入群~")
        })
    }

    shiro.eventChannel.subscribeAlways<MemberLeaveEvent> {
        when (this) {
            is MemberLeaveEvent.Kick -> {
                group.sendMessage("可怜的${member.nick}(${member.id})，被${operator?.nick}玩弄于手掌")
            }

            is MemberLeaveEvent.Quit -> {
                group.sendMessage("${member.nick}(${member.id}) 跑路了")
            }
        }
    }
}

private fun markRecalledMessages() {
    shiro.eventChannel.subscribeAlways<MessageRecallEvent> { event ->
        val qq = when (event) {
            is MessageRecallEvent.FriendRecall -> event.operatorId
            is MessageRecallEvent.GroupRecall -> event.group.id
            else -> return@subscribeAlways
        }
        transaction(database) {
            History.update({
                (History.qq eq qq) and (History.serverId eq event.messageIds.joinToString())
            }) {
                it[recalled] = 1
            }
        }
    }
}
