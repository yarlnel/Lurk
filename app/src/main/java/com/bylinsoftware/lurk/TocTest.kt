package com.bylinsoftware.lurk

import com.bylinsoftware.lurk.dataSource.tocDataSource
import com.bylinsoftware.lurk.dataSource.url
import com.bylinsoftware.lurk.gson.getGsonBuilder

private var counter = 0
fun main() {
    val result = tocDataSource("$url/КНДР")
        .subscribe({
            println(getGsonBuilder().toJson(it))
        },{
            it.printStackTrace()
        },{

        })
}
/* when (it) {
               /*is H2 -> {
                  if (it.content == "") println("jopa nom: ${counter++}")
                   else println(it)
               }

               is H3 -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }

               is Img -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }

               is P -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }

               is QuoteTiny -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }

               is Quote -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)

               }

               is Plashka -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }

               is Li -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }
               is QuoteNoName -> {
                   if (it.content == "") println("jopa nom: ${counter++}") else println(it)
               }*/
           }*/