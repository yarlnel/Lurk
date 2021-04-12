package com.bylinsoftware.lurk.simple_tests


import com.bylinsoftware.lurk.dataSource.url
import com.bylinsoftware.lurk.gson.getGsonBuilder
import com.bylinsoftware.lurk.models.Img
import com.bylinsoftware.lurk.utils.pln
import org.jsoup.Jsoup

fun main() {
    val doc = Jsoup.connect("$url/Zalgo")
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    doc.body().select("ul.gallery")[0] // selecting box with gallery images
        .select("li.gallerybox").forEach { liWithImageBox ->
            var text = ""
            var refWithHighResolutionImage = url
            liWithImageBox.select("div > div.gallerytext")[0].text()?.let { imageText ->
                if (imageText.isNotEmpty()) text = imageText
            }
            liWithImageBox.select("div > div > div > a.image")?.let { aWithHighResolutionImageUrl ->
                refWithHighResolutionImage += aWithHighResolutionImageUrl.attr("href")
            }
            val imageRef = url + liWithImageBox.select("div > div > div > a.image > img")[0].attr("src")
            val listOfHrefs = liWithImageBox.select("div > div.gallerytext > p > a").map { a ->
                a.text() to url + a.attr("href")
            }
            println(getGsonBuilder().toJson(Img(imgUrl = imageRef, content = text, listOfHrefs = listOfHrefs, highResolutionImageUrl = refWithHighResolutionImage)))
        }
}