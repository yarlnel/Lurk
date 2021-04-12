package com.bylinsoftware.lurk.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bylinsoftware.lurk.db.article.Article
import com.bylinsoftware.lurk.db.article.ArticleDao
import com.bylinsoftware.lurk.db.history.History
import com.bylinsoftware.lurk.db.history.HistoryDao

@Database(entities = [History::class, Article::class], version = 2)
abstract class MainDatabase : RoomDatabase()
{
    abstract fun historyDao() : HistoryDao

    abstract fun articleDao() : ArticleDao
}