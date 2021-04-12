package com.bylinsoftware.lurk.db.article

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "article")
data class Article (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "elements") val elementsJson: String,
    @ColumnInfo(name = "toc_info") val tocInfoJson: String
)

