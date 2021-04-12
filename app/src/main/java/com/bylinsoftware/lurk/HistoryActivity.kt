package com.bylinsoftware.lurk

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bylinsoftware.lurk.adapters.HistoryAdapter
import com.bylinsoftware.lurk.db.history.HistoryDao
import com.bylinsoftware.lurk.db.MainDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_toc.list_la

class HistoryActivity : AppCompatActivity()
{
private lateinit var db : MainDatabase
private lateinit var historyDao : HistoryDao

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        db = Room.databaseBuilder(applicationContext, MainDatabase::class.java, "database")
            .allowMainThreadQueries().build()
        historyDao = db.historyDao()
        fab_delete.setOnClickListener {
            AlertDialog.Builder(this@HistoryActivity)
                .setTitle("Удалить всю историю?")
                .setMessage("Удалить всю историю?")
                .setPositiveButton("да") { _, _ ->
                    // yes ammo know this bad but ammo pohuj
                    historyDao.deleteAll(*historyDao.getHistories())

                    Snackbar
                           .make(history_root, """
                               Мы стерли с лица земли всю историю ...
                               От мая, до атстеков ...
                           """.trimIndent(),Snackbar.LENGTH_LONG)
                                     .setAction("Action", null)
                                     .show()
                    list_la.removeAllViews()
                    render()
                }
                .setNegativeButton("нет") { _, _ ->}
                .show()
        }
        render()

    }
    private fun render()
    {
        history_rv.layoutManager = LinearLayoutManager(this)
        history_rv.itemAnimator = DefaultItemAnimator()
        history_rv.adapter = HistoryAdapter(historyDao.getHistories().reversed(), this)
    }

}
