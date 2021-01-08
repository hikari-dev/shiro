package dev.hikari.database

import org.jetbrains.exposed.dao.id.IntIdTable

object History : IntIdTable("history") {
    val serverId = varchar("s_id", 50)
    val time = varchar("time", 30)
    val qq = long("qq")
    val groupQQ = long("group_qq")
    val content = text("content")
    val nameCard = varchar("name_card", 50)
    val nick = varchar("nick", 50)
    val recalled = integer("recalled")
}