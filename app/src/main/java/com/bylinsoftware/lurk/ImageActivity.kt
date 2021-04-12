package com.bylinsoftware.lurk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bylinsoftware.lurk.utils.HasLogSystem
import com.bylinsoftware.lurk.utils.log
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_image.*
import org.jsoup.Jsoup

class ImageActivity : AppCompatActivity(), HasLogSystem {
    private val disposeBag = CompositeDisposable()
    override val tag = ImageActivity::class.java.simpleName
    override val logActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val url = intent.getStringExtra("img_page_url") ?: ""
        val res =
            Single.create<String> { w ->
                val doc = Jsoup.connect(url)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .get()
                doc.body().select("div.fullImageLink > a").first()?.let {
                    log("https:${it.attr("href")}"); w.onSuccess("https:${it.attr("href")}")
                }
             }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                log("full_img_url:: -> $it")
                Picasso.with(this).apply { isLoggingEnabled = true }.load(it).into(big_iv)
            }, { it.printStackTrace() })
        disposeBag.add(res)
    }


    override fun onDestroy() {
        super.onDestroy()
        disposeBag.clear()
    }
}
