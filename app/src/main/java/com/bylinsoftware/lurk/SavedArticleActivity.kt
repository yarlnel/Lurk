package com.bylinsoftware.lurk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.room.Room
import com.bylinsoftware.lurk.db.MainDatabase
import com.bylinsoftware.lurk.utils.makeSpannable
import kotlinx.android.synthetic.main.activity_toc.*

class SavedArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toc)
        val db = Room.databaseBuilder(applicationContext, MainDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
        // TODO TODO TODO
        db.articleDao().getArticles().forEach { article ->
            val view = layoutInflater.inflate(R.layout.text_p, list_la, false)
            val tv = view.findViewById<TextView>(R.id.tv_p)
            tv . text = makeSpannable(text = article.title, listOfHrefs = listOf(article.title to article.title)) { _, ref ->
                val intent = Intent(this@SavedArticleActivity, MainActivity::class.java)
                intent.putExtra("db_article_elements_json", article.elementsJson)
                intent.putExtra("db_article_toc_info_json", article.tocInfoJson)
                startActivity(intent)
            }
            tv . movementMethod = LinkMovementMethod . getInstance ()
            list_la . addView (view)
        }
    }
}
