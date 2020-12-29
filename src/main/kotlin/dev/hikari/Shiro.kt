package dev.hikari

import com.charleskorn.kaml.Yaml
import dev.hikari.config.Config
import dev.hikari.receiver.MessageReceiver
import dev.hikari.quartz.Scheduler
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol.ANDROID_PAD
import java.io.File

val config: Config by lazy {
    val configStr = File("shiroConfig.yml").readText()
    Yaml.default.decodeFromString(Config.serializer(), configStr)
}

val shiro by lazy {
    BotFactory.newBot(config.bot.qq, config.bot.password) {
        fileBasedDeviceInfo()
        protocol = ANDROID_PAD
    }
}

val logger by lazy { shiro.logger }

fun main(): Unit = runBlocking {

    shiro.alsoLogin()

    MessageReceiver.handleMessages()

    Scheduler.startSchedule()
}