package com.bylinsoftware.lurk.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// анотации на случай если мы захотим сохранять статьи
@Entity(tableName = "dynamic_element") data class DynamicElement (
    @PrimaryKey(autoGenerate = true) val index: Int = 0,
    @ColumnInfo(name = "list_of_hrefs") val listOfHrefs: List<Pair<String, String>>?,
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "type") val type: String = "",
    @ColumnInfo(name = "author") var author: String = "",
    @ColumnInfo(name = "img_url") val imgUrl: String = "",
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "id") val id: String = ""
)