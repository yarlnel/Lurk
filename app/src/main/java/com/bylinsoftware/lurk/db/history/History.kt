package com.bylinsoftware.lurk.db.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History") data class History (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "href") val href: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "y") val y: Int
)