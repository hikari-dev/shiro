package dev.hikari.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import dev.hikari.api.Api
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.message.data.At
import kotlin.coroutines.CoroutineContext

class ZuAnCommand(groupQQ: Long) : CliktCommand(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val m: Long by argument(help = "target qq").long()
    private val g: Long by option(help = "target group qq").long().default(groupQQ)
    private val t: Int by option(help = "Number of greetings").int().default(1)

    override fun run() {
        repeat(t) {
            launch {
                shiro.getGroup(g)?.sendMessage(At(m) + "\n" + Api.getZuAnSentence())
            }
        }
    }
}