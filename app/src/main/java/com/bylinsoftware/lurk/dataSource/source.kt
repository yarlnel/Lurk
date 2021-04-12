package com.bylinsoftware.lurk.dataSource

import android.util.Log
import com.bylinsoftware.lurk.gson.getGsonBuilder
import com.bylinsoftware.lurk.models.*
import com.bylinsoftware.lurk.utils.clearAllSymbols

import com.bylinsoftware.lurk.utils.makeCssStyleMap
import com.bylinsoftware.lurk.utils.makeReference
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import java.io.IOException

const val url = "https://lurkmore.to"
private const val TAG = "tag"
/**

 */
fun titleSource (path: String) : Single<String> = Single.create{
    val doc = Jsoup.connect(path)
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    doc.body().select("h1.firstHeading")[0].select("span").text().let(it::onSuccess)
}

fun highResolutionImageSource(url: String): Single<String> = Single.create<String> { w ->
    try {
        val doc = Jsoup.connect(url)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .get()
        doc.body().select("div.fullImageLink > a").first()
            ?.let { element ->
                w.onSuccess("https:${element.attr("href")}")
            }
    } catch (e: java.lang.Exception) {
        w.onError(e)
    }
}

fun stylesheetSource (path: String) : Single<List<Pair<String, String>>> = Single.create{ w ->
    val doc = Jsoup.connect(path)
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    val styleList = mutableListOf<Pair<String, String>>()
    doc.head().select("style").let {
        it.toString().split("\n")
            .filter {line -> "color" in line}
            .filter {line -> ".python.source-python" in line }.forEach { line ->
            line.split(" ").let { word ->
                println(getGsonBuilder().toJson(word))
               if (word.size > 1)
                    if (word[1].length == 4 && word[1].contains("."))
                        when {
                            word[3].length == 9 -> styleList.add(word[1].replace(".", "") to word[3].clearAllSymbols("; }"))
                            "black" in word[3] -> styleList.add(word[1].replace(".", "") to "#2e2e2e")
                            "red" in word[3] -> styleList.add(word[1].replace(".", "") to "#df7474")
                            "green" in word[3] -> styleList.add(word[1].replace(".", "") to "#8bc34a")
                        }

            }
        }
    }
    w.onSuccess(styleList)
}

/*
when {
                            word[3].length == 9 -> styleList.add(word[1].replace(".", "") to word[3].clearAllSymbols("; }"))
                            "black" in word[3] -> styleList.add(word[1].replace(".", "") to "#2e2e2e")
                            "red" in word[3] -> styleList.add(word[1].replace(".", "") to "#df7474")
                            "green" in word[3] -> styleList.add(word[1].replace(".", "") to "#8bc34a")
                        }
 */
fun contentSource(path: String, android: Boolean) : Observable<ArticleElement> = Observable.create { w ->
    try {
        val doc = Jsoup.connect(path)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .get()
        // to select all elements
        //val elements = doc.body().select("#mw-content-text")[0].allElements

        // #mw-content-text
        doc.body().select("#mw-content-text")[0].children().forEach { it ->


            when {

                it.tagName() == "table" && it.className() == "lm-plashka" -> {
                    val td = it.select("tbody > tr > td")
                    val imgUrl = url + td[0].select("a > img").attr("src")
                    val title = td[1].select("b").text()
                    val content = td[1].text()
                    val listOfHrefs = td[1].select("a")
                        .map { element -> element.text() to element.attr("href").makeReference() }
                    w.onNext(
                        Plashka(
                            imgUrl = imgUrl,
                            title = title,
                            content = content,
                            listOfHrefs = listOfHrefs
                        )
                    )
                }

                it.tagName() == "ul" -> {
                    it.children().forEach { e ->
                        if ("toclevel-1" !in e.className() && "toclevel-2" !in e.className() && "toclevel-3" !in e.className())
                            w.onNext(Li(content = e.text(), listOfHrefs = e.select("a").map { element ->
                                element.text() to element.attr("href").makeReference()
                            }))
                    }
                }
                it.tagName() == "p" -> {
                    val listOfHrefs = it.select("a")
                        .map { e -> e.text() to e.attr("href").makeReference() }
                    if (it.text().isNotEmpty()) w.onNext(
                        P(
                            listOfHrefs = listOfHrefs,
                            content = it.text()
                        )
                    )
                }
                it.tagName() == "table" && it.className() == "tpl-quote-tiny" -> {
                    it.select("tbody > tr > td")[1]?.let { td ->
                        if (it.select("tbody > tr").size > 1) w.onNext(
                            QuoteTiny(
                                listOfHrefs = td.select("p > a").map { e ->
                                    e.text() to e.attr("href").makeReference()
                                },
                                content = td.text(),
                                authorHrefs = it.select("tbody > tr")[1].select("td > a")
                                    .map { e -> e.text() to e.attr("href").makeReference() },
                                author = it.select("tbody > tr")[1].select("td").text()
                            )
                        ) else w.onNext(
                            QuoteNoName(
                                content = td.text(),
                                listOfHrefs = td.select("a").map { e ->
                                    e.text() to e.attr("href").makeReference()
                                })
                        )
                    }
                }
                it.tagName() == "table" && it.className() == "tpl-quote" -> {
                    it.select("tbody > tr > td")[0]?.let { td ->
                        if (it.select("tbody > tr").size > 1) w.onNext(
                            Quote(
                                content = td.text(),
                                listOfHrefs = td.select("p > a").map { e ->
                                    e.text() to e.attr("href").makeReference()
                                },
                                authorHrefs = it.select("tbody > tr")[1].select("td > a")
                                    .map { e -> e.text() to e.attr("href").makeReference() },
                                author = it.select("tbody > tr")[1].select("td").text()
                            )
                        )
                        else w.onNext(
                            QuoteNoName(
                                content = td.text(),
                                listOfHrefs = td.select("a").map { e ->
                                    e.text() to e.attr("href").makeReference()
                                })
                        )
                    }
                }


                it.tagName() == "div" && it.className() == "thumb tright" -> {
                    if (android)

                    // какжись я начинаю понимать почему у меня не работала ссылка
                    // listOfHrefs = it.select("span > aКНДР") wtf

                    if (it.select("div.thumbinner > a > img").hasAttr("src"))
                        if (it.select("div.thumbinner > a > img").attr("src").isNotEmpty())
                            if ("Attention32.png" !in it.select("div > a > img").attr("src")) {
                                if (it.select("div > a.image").size != 0) {
                                    w.onNext(
                                        Img(
                                            imgUrl = it.select("div > a > img").attr("src")
                                                .makeReference(),
                                            content = it.select("div.thumbinner > div.thumbcaption").text(),
                                            listOfHrefs = it.select("a.mw-redirect")
                                                .map { a ->
                                                    a.text() to a.attr("href").makeReference()
                                                },
                                            highResolutionImageUrl = "$url${
                                                it.select("div > a.image").first().attr("href")
                                            }"
                                        )
                                    )
                                } else {
                                    it.select("div.embed-placeholder").attr("style")
                                        ?.let { styleString ->
                                            val styleMap = styleString.makeCssStyleMap()
                                            styleMap["background-image"]
                                                ?.replace("url", "")
                                                ?.replace("(", "")
                                                ?.replace(")", "")
                                                ?.let { url ->
                                                    w.onNext(
                                                        VideoBox(
                                                            content = it.select("div.thumbcaption")
                                                                .text(),
                                                            listOfHrefs = it.select("div.thumbcaption > a")
                                                                .map { e ->
                                                                    e.text() to e.attr("href")
                                                                        .makeReference()
                                                                },
                                                            videoUrl = it.select("div.thumbcaption > div > a")
                                                                .first().attr("href"),
                                                            videoImg = if (url != "//i4.ytimg.com/vi/vvM3_Eyd8qg/hqdefault.jpg") "https:$url"
                                                            else "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcShxdWrOYqp-itf7Qv5gORVLSozxjIaj2uIz98CRQAtAKsJsTli&usqp=CAU"
                                                        )
                                                    )
                                                }
                                        }

                                }
                            }
                }

                it.tagName() == "h3" -> {
                    w.onNext(
                        H3(
                            content = it.select("span.mw-headline").text(),
                            id = it.select("span.mw-headline").attr("id")
                        )
                    )

                }

                it.tagName() == "h2" -> {
                    w.onNext(
                        H2(
                            content = it.select("span.mw-headline").text(),
                            id = it.select("span.mw-headline").attr("id")
                        )
                    )
                }


            }
        }
        w.onNext(H2(content = "Галлерея: ", id = "Галлерея"))
    try {
        doc.body().select("ul.gallery")[0] // selecting box with gallery images
            .select("li.gallerybox").forEach { liWithImageBox ->
                var text = ""
                var refWithHighResolutionImage = url
                liWithImageBox.select("div > div.gallerytext")[0].text()?.let { imageText ->
                    if (imageText.isNotEmpty()) text = imageText
                }
                liWithImageBox.select("div > div > div > a.image")
                    ?.let { aWithHighResolutionImageUrl ->
                        refWithHighResolutionImage += aWithHighResolutionImageUrl.attr("href")
                    }
                val imageRef =
                    url + liWithImageBox.select("div > div > div > a.image > img")[0].attr("src")
                val listOfHrefs = liWithImageBox.select("div > div.gallerytext > p > a").map { a ->
                    a.text() to url + a.attr("href")
                }
                w.onNext(
                    Img(
                        imgUrl = imageRef,
                        content = text,
                        listOfHrefs = listOfHrefs,
                        highResolutionImageUrl = refWithHighResolutionImage
                    )
                )
            }
    } catch (e: Exception) {
      e.printStackTrace()
    }

        w.onComplete()
    } catch (ioe: IOException) {
        ioe.printStackTrace()
    }
}

data class SearchPageData (
    val titleMatches: List<Pair<String, String>>,
    val textMatches: List<Pair<String, String>>
)

fun searchMatchesSource(searchText: String) : Single<SearchPageData> = Single.create{ w ->
    val searchDoc = Jsoup
        .connect("https://lurkmore.to/index.php" +
                "?title=%D0%A1%D0%BB%D1%83%D0%B6%D0%B5%D0%B1%D0%BD%D0%B0%D1%8F%3ASearch" +
                "&search=${searchText.replace(" ", " + ")}" +
                "&fulltext=%D0%9D%D0%B0%D0%B9%D1%82%D0%B8")

        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    val titleMatches = mutableListOf<Pair<String, String>>()
    searchDoc.select("ul.mw-search-results")[0].children().forEach { e ->
        val text = e.select("div > a")[0].text()
        val href = e.select("div > a")[0].attr("href")
        titleMatches.add(text to href)
    }
    val textMatches = mutableListOf<Pair<String, String>>()
    searchDoc.select("ul.mw-search-results")[1].children().forEach { e ->
        val text = e.select("div > a")[0].text()
        val href = e.select("div > a")[0].attr("href")
        textMatches.add(text to href)
    }
    w.onSuccess(SearchPageData(
        titleMatches    = titleMatches,
        textMatches     = textMatches
    ))
}


fun tocDataSource(uri: String) : Observable<ArticleElement> = Observable.create{
    val doc = Jsoup.connect(uri)
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    it.onNext(TocTitle(title = doc.select("#toctitle")[0].select("h2").text(), content = ""))
    doc.select("table.toc")[0].select("tbody > tr > td > ul > li").forEach { li ->
        if(li.children().size == 1) it.onNext(
            TocLi(listOfHrefs = listOf(li.select("a > span.toctext").text() to li.select("a").attr("href").removePrefix("#")),
                id = li.select("a > span.tocnumber").text()
                ,  content = li.select("a > span.toctext").text())
        ) else  {
            it.onNext(
                TocLi(listOfHrefs = listOf(
                    li.select("a")[0].child(1).text() to li.select("a").attr("href").removePrefix("#")),
                    id = li.select("a > span.tocnumber").text()
                    , content = li.select("a")[0].child(1).text())
            )
            li.select("ul > li").forEach { e ->
                it.onNext(
                    MiniLi(listOfHrefs = listOf(
                        e.select("a > span.toctext").text() to e.select("a").attr("href").removePrefix("#")),
                        id = e.select("a > span.tocnumber").text()
                        , content = e.select("a > span.toctext").text())
                )
            }
        }

    }
    it.onComplete()
}