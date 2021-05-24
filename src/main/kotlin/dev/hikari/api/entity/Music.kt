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
    val albummid: String,
    val songmid: String,
    val songname: String,
    val singer: List<Singer>,
    val media_mid: String,
    val songid: Int
)

@Serializable
data class Singer(
    val name: String
)

@Serializable
data class QQMusicPlay(
    val code: Int,
    val req_0: Req0
)

@Serializable
data class Req0(
    val data: Data
)

@Serializable
data class Data(
    val midurlinfo: List<UrlInfo>
)

@Serializable
data class UrlInfo(
    val purl: String
)
