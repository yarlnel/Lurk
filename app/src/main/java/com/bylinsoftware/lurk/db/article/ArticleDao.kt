package com.bylinsoftware.lurk.db.article

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao interface ArticleDao
{
    @Query("SELECT title FROM article") fun getTitles () : List<String>

    @Query("DELETE FROM article WHERE title = :title") fun deleteByTitle (title: String)

    @Query("SELECT * FROM article WHERE title = :title") fun getArticleByTitle (title: String) : Article

    @Insert fun insertArticle(article: Article)

    @Query("SELECT * FROM article") fun getArticles () : List<Article>

}