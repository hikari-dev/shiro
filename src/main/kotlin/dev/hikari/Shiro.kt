package dev.hikari

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.hikari.config.Config
import dev.hikari.quartz.startSchedule
import dev.hikari.receiver.handleMessages
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol.ANDROID_PAD
import java.io.File

val config: Config by lazy {
    val configStr = File("shiroConfig.yml").readText()
    Yaml(configuration = YamlConfiguration(strictMode = false)).decodeFromString(Config.serializer(), configStr)
}

val shiro by lazy {
    BotFactory.newBot(config.qqBot.qq, config.qqBot.password) {
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