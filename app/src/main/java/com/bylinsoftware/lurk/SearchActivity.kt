package com.bylinsoftware.lurk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView


import com.bylinsoftware.lurk.dataSource.searchMatchesSource
import com.bylinsoftware.lurk.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), HasLogSystem
{
    private val url = "https://lurkmore.to"
    private val disposeBag = CompositeDisposable()

    private fun addTextRefLine(it: Pair<String, String>)
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

    private fun String.addStringToRecyclerView()
    {
        layoutInflater.inflate(R.layout.h3, scroll_la, false).let {view ->
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = this
            scroll_la.addView(view)
        }
    }

    private fun generateSearchResultUi(searchText: String)
    {
        progress_bar_of_search_page.visible()
        scroll_la.removeAllViews()
        val res = searchMatchesSource(searchText = searchText)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ spd /* search page data*/ ->
                "Совпадения в заголовках статьи".addStringToRecyclerView()

                spd.titleMatches.forEach(::addTextRefLine)

                "Совпадения в тексте статей".addStringToRecyclerView()

                spd.textMatches.forEach(::addTextRefLine)

            }, {
                progress_bar_of_search_page.invisible()
                log("error from $tag ->\n${it.stackTrace}")
            })
        disposeBag.add(res)
        progress_bar_of_search_page.invisible()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                generateSearchResultUi(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                generateSearchResultUi(query)
                return true
            }
        })
    }

    override fun onDestroy()
    {
        super.onDestroy()
        disposeBag.clear()
    }

    override val tag: String
        get() = SearchActivity::class.java.simpleName

    override val logActive: Boolean
        get() = false

}