package com.bylinsoftware.lurk.simple_tests

import com.bylinsoftware.lurk.dataSource.contentSource
import com.bylinsoftware.lurk.dataSource.url
import com.bylinsoftware.lurk.gson.getGsonBuilder
import com.bylinsoftware.lurk.models.*

val listOfElement = mutableListOf<ArticleElement>()

inline fun <reified T> findByType()
{
    listOfElement.filterIsInstance<T>().forEach { element -> println(getGsonBuilder().toJson(element)) }
}
fun main() {
    val res = contentSource("$url/Python", false)
        .subscribe({ element ->
            listOfElement.add(element)
            println(getGsonBuilder().toJson(element))
        }, {
            it.printStackTrace()
        }, {
            println("end of this page")
        })
    while (true) {
        readLine().toString().let { command ->
            when {
                "find_with_type"  in command || "fwt" in command -> command.split(" ")[1].let { type ->
                    when (type) {
                        "img" -> findByType<Img>()
                        "h2" -> findByType<H2>()
                        "h3" -> findByType<H3>()
                        "qnn" -> findByType<QuoteNoName>()
                        "q" -> findByType<Quote>()
                        "qt" -> findByType<QuoteTiny>()
                        "plashka" -> findByType<Plashka>()
                        "p" -> findByType<P>()
                        "li" -> findByType<Li>()
                        "mini_li" -> findByType<MiniLi>()
                        "code_box" -> findByType<CodeBox>()
                        "video_box" -> findByType<VideoBox>()
                    }
                }

            }
        }
    }
}