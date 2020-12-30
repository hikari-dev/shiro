package dev.hikari.receiver

import FixedStack
import dev.hikari.api.Api
import dev.hikari.config
import dev.hikari.shiro
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.ids
import java.util.*

private val groupMap = hashMapOf<Long, FixedStack<GroupMessageEvent>>()

/**
 * Handle all the messages shiro received.
 */
fun handleMessages() {
    storeMessagesToDatabase()

    handleFriendMessages()

    handleGroupMessages()

    notifyRecall()
}

fun storeMessagesToDatabase() {
    // TODO: 2020/12/30 Store message to mysql db.

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
    shiro.eventChannel.subscribeAlways<GroupMessageEvent> { event ->
        val groupNumber = event.group.id
        if (groupMap[groupNumber] == null) {
            groupMap[groupNumber] = FixedStack(200)
        }
        groupMap[groupNumber]!!.push(event)
    }
}

fun notifyRecall() {
    shiro.eventChannel.subscribeAlways<MessageRecallEvent> {
        when (it) {
            is MessageRecallEvent.GroupRecall -> {
                val stack = groupMap[it.group.id] ?: return@subscribeAlways
                for (event in stack) {
                    if (messageIds.isNotEmpty() && messageIds.contentEquals(event.message.ids)) {
                        // TODO: 2020/12/30 https://github.com/mamoe/mirai/issues/307
//                shiro.getFriend(config.masterQQ)?.sendMessage(event.message)
                        shiro.getFriend(config.masterQQ)?.sendMessage(buildMessageChain {
                            +"群 ${it.group.name}(${it.group.id}) 的 ${it.operator?.nameCardOrNick}(${it.operator?.id}) 撤回了一条消息\n"
                            +"消息id为 ${messageIds.joinToString(prefix = "[", postfix = "]")}"
                        })
                    }
                }
            }
            is MessageRecallEvent.FriendRecall -> {

            }
        }
    }
}
