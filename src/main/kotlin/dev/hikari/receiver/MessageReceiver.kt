package dev.hikari.receiver

import dev.hikari.shiro
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content

object MessageReceiver {

    fun handleMessages() {
        //handle friend message event
        shiro.eventChannel.subscribeAlways<FriendMessageEvent> {
            println("收到好友 ${it.sender.nick}(${it.sender.id}) 发送的消息 - ${it.message.content}")
        }

        //handle group message event
        shiro.eventChannel.subscribeAlways<GroupMessageEvent> {
            println("收到群 ${it.group.name}(${it.group.id}) 的 ${it.sender.nameCardOrNick}(${it.sender.id}) 发送的消息 - ${it.message.content}")
        }
    }

}