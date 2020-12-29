package dev.hikari.quartz

import dev.hikari.config
import dev.hikari.logger
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            shiro.getGroup(config.testGroup)?.sendMessage("Good morning!")
        }
    }
}