package dev.hikari

import dev.hikari.quartz.startSchedule
import dev.hikari.receiver.handleMessages
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLogger
import top.mrxiaom.overflow.BotBuilder

lateinit var shiro: Bot
lateinit var logger: MiraiLogger

fun main(): Unit = runBlocking {
    shiro = BotBuilder.positive("ws://127.0.0.1:3001")
        .connect() ?: return@runBlocking

    logger = shiro.logger

    handleMessages()

    startSchedule()
}

