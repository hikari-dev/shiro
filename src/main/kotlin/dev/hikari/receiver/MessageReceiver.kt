package dev.hikari.receiver

import dev.hikari.api.Api
import dev.hikari.shiro
import net.mamoe.mirai.event.subscribeGroupMessages

/**
 * Handle all the messages shiro received.
 */
fun handleMessages() {

    storeMessagesToDatabase()

    handleFriendMessages()

    handleGroupMessages()
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
}
