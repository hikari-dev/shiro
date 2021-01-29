package dev.hikari.api.entity

import kotlinx.serialization.Serializable

@Serializable
data class QQMusicSearch(
    val code: Int,
    val data: QQMusicSearchData
)

@Serializable
data class QQMusicSearchData(
    val keyword: String,
    val song: SongList
)

@Serializable
data class SongList(
    val list: List<Song>
)

@Serializable
data class Song(
    val f: String
)
