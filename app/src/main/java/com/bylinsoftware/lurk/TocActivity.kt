package com.bylinsoftware.lurk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.bylinsoftware.lurk.dataSource.tocDataSource
import com.bylinsoftware.lurk.models.MiniLi
import com.bylinsoftware.lurk.models.TocLi
import com.bylinsoftware.lurk.models.TocTitle
import com.bylinsoftware.lurk.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_toc.*

class TocActivity : AppCompatActivity()
{
    private val disposeBag = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toc)
        val logger = Logger(
            tag = TocActivity::class.java.simpleName,
            from = TocActivity::class.java.simpleName,
            doWithLog = pb_toc::invisible
        )
        pb_toc.visible()
        intent.getStringExtra("uri")?.let {uri ->
            val res = tocDataSource(uri)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        when(it) {
                               is TocLi -> {
                                    val view = layoutInflater.inflate(R.layout.text_p, list_la, false)
                                    val tv = view.findViewById<TextView>(R.id.tv_p)
                                    tv . text = makeSpannable(text = it.content, listOfHrefs = it.listOfHrefs) { _, ref ->
                                        val intent = Intent()
                                        intent.putExtra("id", ref)
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    }
                                    tv . movementMethod = LinkMovementMethod . getInstance ()
                                    list_la . addView (view)
                               }
                                is MiniLi -> {
                                    val view = layoutInflater.inflate(R.layout.li_la, list_la, false)
                                    val tv = view.findViewById<TextView>(R.id.tv_li)
                                    val num = view.findViewById<TextView>(R.id.tv_num)
                                    num . text = it.id.split(".")[1]
                                    tv . text = makeSpannable(text = it.content, listOfHrefs = it.listOfHrefs) { _, ref ->
                                        val intent = Intent()
                                        intent.putExtra("id", ref)
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    }
                                    tv . movementMethod = LinkMovementMethod . getInstance ()
                                    list_la . addView (view)
                                }
                                is TocTitle -> {
                                    val view = layoutInflater.inflate(R.layout.h, list_la, false)
                                    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                                    tvTitle.text = it.title
                                    list_la.addView(view)
                                }
                        }
                    }
                }, {
                    logger.log(it)
                    pb_toc.invisible()
                }, pb_toc::invisible)
            disposeBag.add(res)
        }
    }


    override fun onDestroy()
    {
        super.onDestroy()
        disposeBag.clear()
    }


}
