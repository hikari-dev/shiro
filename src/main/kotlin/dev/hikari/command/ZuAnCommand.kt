package dev.hikari.command

import dev.hikari.api.Api
import dev.hikari.shiro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import kotlin.coroutines.CoroutineContext

object ZuAnCommand : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun parse(args: List<String>, groupId: Long) {
        var targetQQ: Long = -1
        var groupQQ: Long = groupId
        var repeatTimes = 1

        for (arg in args) {
            when {
                arg.startsWith("--t=") -> {
                    arg.removePrefix("--t=").toLongOrNull()?.let { targetQQ = it }
                }
                arg.startsWith("--g=") -> {
                    arg.removePrefix("--g=").toLongOrNull()?.let { groupQQ = it }
                }
                arg.startsWith("--r=") -> {
                    arg.removePrefix("--r=").toIntOrNull()?.let { repeatTimes = if (it <= 10) it else 10 }
                }
            }
        }

        repeat(repeatTimes) {
            launch {
                shiro.getGroup(groupQQ)?.sendMessage(buildMessageChain {
                    if (targetQQ != -1L) {
                        add(At(targetQQ) + "\n")
                    }
                    add(Api.getZuAnSentence())
                })
            }
        }
    }


}