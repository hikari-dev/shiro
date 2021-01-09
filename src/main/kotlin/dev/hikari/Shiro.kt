package dev.hikari

import dev.hikari.config.ShiroConfig
import dev.hikari.quartz.startSchedule
import dev.hikari.receiver.handleMessages
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol.ANDROID_PAD

val shiro by lazy {
    BotFactory.newBot(ShiroConfig.config.qqBot.qq, ShiroConfig.config.qqBot.password) {
        fileBasedDeviceInfo()
        protocol = ANDROID_PAD
    }
}

val logger by lazy { shiro.logger }

fun main(): Unit = runBlocking {

    shiro.alsoLogin()

    handleMessages()

    startSchedule()
}