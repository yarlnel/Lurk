package com.bylinsoftware.lurk.db.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao interface HistoryDao
{
    @Query("SELECT * FROM history") fun getHistories() : Array<out History>

    @Insert fun insertAll(vararg histories: History)

    @Insert fun insert(history: History)

    @Delete fun deleteAll(vararg histories: History)

    @Query("DELETE FROM history") fun deleteAll()

    @Query("SELECT * FROM history ORDER BY id DESC LIMIT 1") fun getLastPage() : History
}