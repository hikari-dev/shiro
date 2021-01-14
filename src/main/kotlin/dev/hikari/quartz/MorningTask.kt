package dev.hikari.quartz

import dev.hikari.config.ShiroConfig
import dev.hikari.logger
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import org.quartz.Job
import org.quartz.JobExecutionContext
import kotlin.coroutines.CoroutineContext

class MorningTask(
    override val coroutineContext: CoroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        logger.error("MorningTask execute error", throwable)
    }
) : Job, CoroutineScope {
    override fun execute(context: JobExecutionContext?) {
        launch {
            val instantBefore = Instant.parse("2020-12-29T06:30:06Z")
            val daysUntil = instantBefore.daysUntil(Clock.System.now(), TimeZone.UTC)
            shiro.getFriend(ShiroConfig.config.masterQQ)?.sendMessage("主人sama早上好，距离我们命运中的相遇已经过去了${daysUntil}天")
        }
    }
}