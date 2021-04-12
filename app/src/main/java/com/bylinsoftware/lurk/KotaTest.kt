package com.bylinsoftware.lurk

import com.bylinsoftware.lurk.dataSource.contentSource
import com.bylinsoftware.lurk.dataSource.url
import com.bylinsoftware.lurk.gson.getGsonBuilder
import com.bylinsoftware.lurk.models.ArticleElement
import java.util.*


fun main()
{

    val elements = mutableListOf<ArticleElement>()
    val scanner = Scanner(System.`in`)
    val res = contentSource("$url/КНДР", false).subscribe({
            println("\n${getGsonBuilder().toJson(it)}\n")
            elements.add(it)
        },{it.printStackTrace()},{})
    loop@ while (true) {
        when (scanner.nextLine()) {
            "stop" -> break@loop
            "contain" -> {
                print("text: ")
                val text = scanner.nextLine()
                elements.filter {it.content == text}.map(getGsonBuilder()::toJson).forEach(::println)
                println("""
                    
                """.trimIndent())
            }
            "find" -> {
                val index = scanner.nextLine().toInt()
                println("""
                    ${getGsonBuilder().toJson(elements[index-2])}
                    ${getGsonBuilder().toJson(elements[index-1])}
                    ${getGsonBuilder().toJson(elements[index])}
                    ${getGsonBuilder().toJson(elements[index+1])}
                    ${getGsonBuilder().toJson(elements[index+2])}
                """.trimIndent())
            }
        }
    }

}
