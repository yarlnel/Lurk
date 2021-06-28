package com.bylinsoftware.lurk

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.room.Room
import com.bylinsoftware.lurk.dataSource.contentSource
import com.bylinsoftware.lurk.dataSource.highResolutionImageSource
import com.bylinsoftware.lurk.dataSource.titleSource
import com.bylinsoftware.lurk.db.history.History
import com.bylinsoftware.lurk.db.MainDatabase
import com.bylinsoftware.lurk.models.*
import com.bylinsoftware.lurk.utils.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), HasLogSystem
{

    private val url = "https://lurkmore.to"
    private lateinit var uri : String
    private val _tag = MainActivity::class.java.simpleName
    private var id: String = ""
    private val ps : MutableList<Pair<ArticleElement, View>> = mutableListOf()
    private var counter = 0
    private val disposeBag = CompositeDisposable()
    private val _img = 5
    private val _toc = 3
    private val articleElements = mutableListOf<ArticleElement>()
    private lateinit var backList : MutableList<View>
    override val tag = MainActivity::class.java.simpleName
    override val logActive = false
    var visibility = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Picasso.with(this@MainActivity).load(R.drawable.goodfon).centerCrop().fit().into(background_iv)
        pb.visibility = ProgressBar.VISIBLE

        // initial Logger system
        val logger = Logger(
            tag = MainActivity::class.java.simpleName,
            from = MainActivity::class.java.simpleName
        )



        // Get DB
        val db = Room.databaseBuilder(applicationContext, MainDatabase::class.java, "database")
            .fallbackToDestructiveMigration() // if we go too migration that's function clear all data for new struct
            .allowMainThreadQueries().build()


        val historyDao = db.historyDao()

        // get url (uri) for article (from pref or intent_extras or def_value)
        uri = intent.getStringExtra("uri") ?: historyDao.getLastPage().href ?: "$url/КНДР"



        // Log saved articles
        /*
        db.articleDao().getArticles().forEach { e ->
            withLogName("article_db") {
                log("Title: "+e.title)
                log("Elements: \n"+e.elementsJson)
                log("Toc: \n"+e.tocInfoJson)
            }
        }
         */


        if (intent.dataString != null) uri = intent.dataString ?: "$url/КНДР"

        // save Title for history list
        val result = titleSource(uri).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                historyDao.insert(
                    History(
                        title = it,
                        href = uri,
                        y = scroll_view.scrollY
                    )
                )
            },logger::log)
        disposeBag.add(result)


        // list of fabs and his tv which must be in background
        backList = mutableListOf<View>(
            fab_search,
            tv_search,
            fab_toc,
            tv_toc,
            fab_history,
            tv_history,
            fab_background,
            tv_background,
            new_back
        )

        backList.makeListOfViewInvisibleAndUnClickable()
        visibility = true

        fab_menu.setOnClickListener {
            visibility = if (visibility) {
                backList.makeListOfViewVisibleAndClickable()
                false
            } else {
                backList.makeListOfViewInvisibleAndUnClickable()
                true
            }
        }



        // change background image from gallery
        fab_background.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Острожно эта функция не доработана")
                .setMessage("Осторжно это приложение в бэте,\nпоэтому некоторые функции не работают нормально !!!")
                .setPositiveButton("Да") {_, _ ->
                    Snackbar
                           .make(root_main, "ну ладно",Snackbar.LENGTH_LONG)
                                     .setAction("Action", null)
                                     .show()
                    startActivityForResult(intent, _img)
                }
                .setNegativeButton("Нет") {_, _ ->}
                .create()
                .show()
        }

        // go to Toc Activity
        fab_toc.setOnClickListener {
            val intent = Intent(this@MainActivity, TocActivity::class.java)
            intent.putExtra("uri", uri)
            startActivityForResult(intent, _toc)
        }

        // go to SearchActivity
        fab_search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // go to HistoryActivity
        fab_history.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.putExtra("uri", uri)
            startActivity(intent)
        }


        //if (intent.getStringExtra("db_article_elements_json") == null) {
            val contentResult = contentSource(uri, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ::renderArticleElement,

                    {

                        Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_LONG).show()

                    }
                    ,
                    {


                    }
                )
            disposeBag.add(contentResult)
    }

    override fun onBackPressed()
    {
        if (main_iv.visibility == ImageView.VISIBLE) {
            listOf(new_back, main_iv, pb).makeInvisible()
        }
        else {
            // edit our url into pref (for saved article into history)
            getPreferences(MODE_PRIVATE).edit().putString("go_uri", uri).apply()

            super.onBackPressed()
        }
    }

    override fun onDestroy()
    {
        // edit our url into pref (for saved article into history)
        getPreferences(MODE_PRIVATE).edit().putString("go_uri", uri).apply()

        super.onDestroy()

        disposeBag.clear()
    }

    // into onActivityResult we get result Data from the TocActivity
    // we get String that we wanna to move to the place of
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            _toc -> moveToPlaceUseIdTitle(id = data?.getStringExtra("id") ?: "")
            _img -> data?.data?.let { setBackgroundWallpaperFromDataUri(it) }
        }
    }

    private fun setBackgroundWallpaperFromDataUri (dataUri: Uri) =
                    Picasso
                            .with(this@MainActivity)
                            .load(dataUri)
                            .centerCrop()
                            .fit()
                            .into(background_iv)

    private fun moveToPlaceUseIdTitle (id: String)
    {
        backList.makeListOfViewInvisibleAndUnClickable()
        visibility = true


        ps.filter { it.first is H2 || it.first is H3 }.find {
            when (it.first) {
                is H2 -> return@find (it.first as H2).id == id
                is H3 -> return@find (it.first as H3).id == id
                else -> false
            }
        }?.let {
            scroll_view.scrollTo(0, it.second.top)
        }
    }



    private fun makeH2(h2: H2)
    {
        val view = layoutInflater.inflate(R.layout.h, ll, false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = h2.content
        ll.addView(view)
        ps.add(h2 to view)
    }

    private fun makeH3(h3: H3)
    {
        val view = layoutInflater.inflate(R.layout.h, ll, false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = "-> ${h3.content}"
        ll.addView(view)
        ps.add(h3 to view)
    }

    private fun makeParagraph(paragraph: P)
    {
        val view = layoutInflater.inflate(R.layout.text_p, ll, false)
        val tv = view.findViewById<TextView>(R.id.tv_p)

        tv.text = makeReference(paragraph.content, paragraph.listOfHrefs)

        tv.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

        private fun makeQuoteTiny(quoteTiny: QuoteTiny)
    {
        val view = layoutInflater.inflate(R.layout.quote, ll, false)
        val tvAuthor = view.findViewById<TextView>(R.id.tv_author)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)

        tvAuthor.text = makeReference(quoteTiny.author, quoteTiny.authorHrefs)

        tvContent.text = makeReference(quoteTiny.content, quoteTiny.listOfHrefs)

        tvContent.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makeQuote(quote: Quote)
    {
        val view = layoutInflater.inflate(R.layout.quote, ll, false)
        val tvAuthor = view.findViewById<TextView>(R.id.tv_author)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)

        tvAuthor.text = makeReference(quote.author, quote.authorHrefs)

        tvContent.text = makeReference(quote.content, quote.listOfHrefs)

        tvContent.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makePlashka (plashka: Plashka)
    {
        val view = layoutInflater.inflate(R.layout.plasha, ll, false)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val ivPlashka = view.findViewById<ImageView>(R.id.iv_plasha)

        tvContent.text = makeReference(plashka.content, plashka.listOfHrefs)

        Picasso
            .with(this)
            .apply { isLoggingEnabled = true }
            .load(plashka.imgUrl)
            .resize(100, 100)
            .centerCrop()
            .into(ivPlashka)
        tvTitle.text = plashka.title
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun ImageView.setHighResolutionImageFromUrl (url: String)
    {
        val res =
            highResolutionImageSource(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ highResolutionUrl ->
                    Picasso.with(baseContext).apply { isLoggingEnabled = true }.load(highResolutionUrl).into(main_iv)
                    listOf(pb).makeInvisible()
                }, { error -> error.printStackTrace() })
        disposeBag.add(res)
    }

    private fun changeImagePicture (imageView: ImageView, url: String)
    {
        imageView.setOnClickListener { _ ->
            main_iv.setImageDrawable(null)
            listOf(main_iv, new_back, pb).makeVisible()
            main_iv.setOnClickListener { _ ->
                main_iv.setImageDrawable(null)
                listOf(main_iv, new_back).makeInvisible()
            }
            main_iv.setHighResolutionImageFromUrl(url = url)
        }
    }

    private fun makeImg(img: Img)
    {
        val view = layoutInflater.inflate(R.layout.img_layout, ll, false)
        val ivImg = view.findViewById<ImageView>(R.id.img_cvad)
        val tvDesc = view.findViewById<TextView>(R.id.tv_description)

        Picasso.with(this).apply { isLoggingEnabled = true }.load(img.imgUrl).into(ivImg)

        changeImagePicture(ivImg, img.highResolutionImageUrl)
        fun makeImgResolutionBetter() {
            val res = highResolutionImageSource(img.highResolutionImageUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ highResolutionUrl ->
                    Picasso.with(baseContext).apply { isLoggingEnabled = true }
                        .load(highResolutionUrl).into(ivImg)

                }, { error ->  error.printStackTrace() })
            disposeBag.add(res)
        }
        makeImgResolutionBetter()


        tvDesc.text = makeReference(img.content, img.listOfHrefs)
        tvDesc.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makeListElement (listElement: Li)
    {
        val view = layoutInflater.inflate(R.layout.li_la, ll, false)
        val tv = view.findViewById<TextView>(R.id.tv_li)
        val num = view.findViewById<TextView>(R.id.tv_num)
        counter++
        num.text = "$counter. "
        tv.text = makeReference(listElement.content, listElement.listOfHrefs)
        tv.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makeQuoteNoName (quoteNoName: QuoteNoName)
    {
        val view = layoutInflater.inflate(R.layout.no_name_quote, ll, false)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)

        tvContent.text = makeReference(quoteNoName.content, quoteNoName.listOfHrefs)

        tvContent.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makeVideoBox (videoBox: VideoBox)
    {
        val view = layoutInflater.inflate(R.layout.video_box, ll, false)
        val imgVideo = view.findViewById<ImageView>(R.id.img_video)
        val imgPlaceholder = view.findViewById<ImageView>(R.id.img_placeholder)
        val tvDesc = view.findViewById<TextView>(R.id.tv_description)

        // add Video preview image into our VideoBox
        Picasso.with(this).apply { isLoggingEnabled = true }
            .load(videoBox.videoImg).into(imgVideo)

        // when user click on imgVideo or imgPlaceholder
        // we send him into youtube or another video service (#vimeo)

        imgVideo.setOnClickListener { _ ->
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(videoBox.videoUrl)))
        }
        imgPlaceholder.setOnClickListener { _ ->
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(videoBox.videoUrl)))
        }

        // make our string clickable and with another color use Spannable Elements in text
        tvDesc.text = makeReference(videoBox.content, videoBox.listOfHrefs)
        tvDesc.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun makeCodeBox (codeBox: CodeBox)
    {
        val view = layoutInflater.inflate(R.layout.no_name_quote, ll, false)
        val tv = view.findViewById<TextView>(R.id.tv_content)

        tv.text = makeColorCodeText(codeBox.content, codeBox.colorData)

        tv.movementMethod = LinkMovementMethod.getInstance()
        ll.addView(view)
    }

    private fun renderArticleElement (element: ArticleElement)
    {
        pb.visibility = ProgressBar.INVISIBLE
        if (element !is Li) counter = 0
        articleElements.add(element)
        when (element) {
            is VideoBox -> makeVideoBox(element)
            is H2 -> makeH2(element)
            is H3 -> makeH3(element)
            is Img -> makeImg(element)
            is P -> makeParagraph(element)
            is QuoteTiny -> makeQuoteTiny(element)
            is Quote -> makeQuote(element)
            is Plashka -> makePlashka(element)
            is Li -> makeListElement(element)
            is QuoteNoName -> makeQuoteNoName(element)
            is CodeBox -> makeCodeBox(element)
        }
    }
}
