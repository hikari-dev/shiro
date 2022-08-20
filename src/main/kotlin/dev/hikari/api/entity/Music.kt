package dev.hikari.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QQMusicSearch(
    val code: Int,
    val data: QQMusicSearchData
)

@Serializable
data class QQMusicSearchData(
    val album: AlbumList,
    val song: SongList
)

@Serializable
data class AlbumList(
    val count: Int,
    @SerialName("itemlist")
    val itemList: List<Album>
)

@Serializable
data class Album(
    @SerialName("docid")
    val docId: String,
    val id: String,
    val mid: String,
    val name: String,
    val pic: String,
    val singer: String
)

@Serializable
data class SongList(
    val count: Int,
    @SerialName("itemlist")
    val itemList: List<Song>
)

@Serializable
data class Song(
    @SerialName("docid")
    val docId: String,
    val id: String,
    val mid: String,
    val name: String,
    val singer: String
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
