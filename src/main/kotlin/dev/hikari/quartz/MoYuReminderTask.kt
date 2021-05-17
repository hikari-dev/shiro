package dev.hikari.quartz

import dev.hikari.logger
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.message.data.Image
import org.quartz.Job
import org.quartz.JobExecutionContext
import kotlin.coroutines.CoroutineContext

class MoYuReminderTask : Job, CoroutineScope {

    private val imageIds = arrayOf(
        "{BD4DA0E5-F67B-D21B-694A-E357D5CBB347}.jpg",
        "{ACBEDCC9-44EA-5DD0-76E9-76E3FE0A6692}.jpg",
        "{0427996B-0079-E62B-B546-31733CBD8077}.jpg"
    )

    override fun execute(context: JobExecutionContext?) {
        launch {
            shiro.groups.forEach {
                it.sendMessage(Image(imageIds.random()))
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            logger.error("MoYuReminderTask execute error", throwable)
        }
}