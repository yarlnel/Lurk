package com.bylinsoftware.lurk.simple_tests

import com.bylinsoftware.lurk.dataSource.stylesheetSource
import com.bylinsoftware.lurk.dataSource.url
import com.bylinsoftware.lurk.gson.getGsonBuilder
import com.bylinsoftware.lurk.utils.clearAllSymbols
import com.bylinsoftware.lurk.utils.makeCssStyleMap
import org.jsoup.Jsoup

fun main() {
     val colorReductionMap = mutableMapOf<String, String>()
     val res = stylesheetSource("$url/Python")
         .subscribe ({ e ->
             e.forEach { pair ->
                 colorReductionMap[pair.first] = pair.second
                 println("(${pair.first} -> ${pair.second})")
             }
         },{
             it.printStackTrace()
         })
    val doc = Jsoup.connect("$url/Python")
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()

   /* doc.head().select("style").let {
        it.toString().split("\n")
            .filter { line -> ".python.source-python" in line && "color" in line}.forEach { line ->
            println()
            println(line.makeCssStyleMap())
            println()
        }
    }*/

   doc.body().select("#mw-content-text")[0].children().forEach {element ->
        when {
            element.tagName() == "div" -> {
                element.select("div > div").let { div ->
                    if (div.hasClass("python")) {
                        div.select("pre").select("span").forEach { span ->
                            println(span.text() to colorReductionMap[span.className()])
                        }
                    }
                }
            }
        }
    }

}

