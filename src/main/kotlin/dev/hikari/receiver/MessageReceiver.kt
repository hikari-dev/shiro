package dev.hikari.receiver

import dev.hikari.api.Api
import dev.hikari.shiro
import net.mamoe.mirai.event.subscribeGroupMessages

/**
 * handle all the messages shiro received
 */
fun handleMessages() {

    handleFriendMessages()

    handleGroupMessages()
}

/**
 * handle all friend message event
 */
fun handleFriendMessages() {

}

/**
 * handle all group message event
 */
fun handleGroupMessages() {
    shiro.eventChannel.subscribeGroupMessages {
        "一言" {
            val hitokoto = Api.getHitokoto()
            group.sendMessage("${hitokoto.hitokoto}\n来自于:${hitokoto.from}")
        }
    }
}

