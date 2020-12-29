package dev.hikari

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol.ANDROID_PAD
import java.io.File

val config: Config by lazy {
    val configStr = File("shiroConfig.yml").readText()
    Yaml.default.decodeFromString(Config.serializer(), configStr)
}

fun main(): Unit = runBlocking {

    val bot = BotFactory.newBot(config.bot.qq, config.bot.password) {
        fileBasedDeviceInfo()
        protocol = ANDROID_PAD
    }.alsoLogin()


    bot.eventChannel.subscribeAlways<GroupMessageEvent> {
        println("收到群 ${it.group.name}(${it.group.id}) 的 ${it.sender.nameCardOrNick}(${it.sender.id}) 发送的消息 - ${it.message.content}")
    }
}