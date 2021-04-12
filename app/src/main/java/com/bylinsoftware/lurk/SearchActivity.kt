package com.bylinsoftware.lurk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast

import com.bylinsoftware.lurk.dataSource.searchMatchesSource
import com.bylinsoftware.lurk.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity()
{
    private val url = "https://lurkmore.to"
    private val disposeBag = CompositeDisposable()

    fun addTextRefLine(it: Pair<String, String>)
    {
        val view = layoutInflater.inflate(R.layout.text_p, scroll_la, false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_p)

        tvTitle.text = makeSpannable(
            text = it.first,
            listOfHrefs = listOf(it.first to it.second)
        ) { _, ref ->
            val intent = Intent(this@SearchActivity, MainActivity::class.java)
            intent.putExtra("uri", url + ref)
            startActivity(intent)
            finish()
        }
        tvTitle.movementMethod = LinkMovementMethod.getInstance()
        scroll_la.addView(view)
    }

    fun String.addStringToRv()
    {
        layoutInflater.inflate(R.layout.h3, scroll_la, false).let {view ->
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = this
            scroll_la.addView(view)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val logger = Logger(
            tag = SearchActivity::class.java.simpleName,
            from = SearchActivity::class.java.simpleName,
            doWithLog = pb_search::invisible
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                pb_search.visible()
                scroll_la.removeAllViews()
                val res = searchMatchesSource(newText)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ spd /* search page data*/ ->
                        "Совпадения в заголовках статьи".addStringToRv()
                        spd.titleMatches.forEach(::addTextRefLine)
                        "Совпадения в тексте статей".addStringToRv()
                        spd.textMatches.forEach(::addTextRefLine)
                    }, { pb_search.invisible(); Toast.makeText(this@SearchActivity, "Search fail, im sorry", Toast.LENGTH_SHORT).show() })
                disposeBag.add(res)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                pb_search.visible()
                scroll_la.removeAllViews()
                val res = searchMatchesSource(query)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ spd /* search page data*/ ->
                        "Совпадения в заголовках статьи".addStringToRv()
                        spd.titleMatches.forEach(::addTextRefLine)
                        "Совпадения в тексте статей".addStringToRv()
                        spd.textMatches.forEach(::addTextRefLine)
                    }, { pb_search.invisible(); Toast.makeText(this@SearchActivity, "Search fail, im sorry", Toast.LENGTH_SHORT).show() })
                disposeBag.add(res)
                return true
            }
        })
    }

    override fun onDestroy()
    {
        super.onDestroy()
        disposeBag.clear()
    }

}