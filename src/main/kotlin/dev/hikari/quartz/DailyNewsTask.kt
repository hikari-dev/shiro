package dev.hikari.quartz

import dev.hikari.api.Api
import dev.hikari.logger
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.quartz.Job
import org.quartz.JobExecutionContext
import kotlin.coroutines.CoroutineContext

class DailyNewsTask(
    override val coroutineContext: CoroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        logger.error("DailyNewsTask execute error", throwable)
    }
) : Job, CoroutineScope {

    override fun execute(context: JobExecutionContext?) {
        launch {
            Api.getDailyNews().use { inputStream ->
                val image = inputStream.uploadAsImage(shiro.asFriend)
                shiro.groups.forEach { group ->
                    group.sendMessage(image)
                }
            }
        }
    }

}