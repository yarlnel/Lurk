package com.bylinsoftware.lurk.simple_tests


import com.bylinsoftware.lurk.MainActivity
import com.bylinsoftware.lurk.dataSource.searchMatchesSource
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    for (i in 0..10) searchMatchesSource(scanner.nextLine()).subscribe({
                    println("""
title matches:
${it.titleMatches.map { e -> return@map e.first}.joinToString("\n")}
                        
text matches:
${it.textMatches.map { e -> return@map e.first}.joinToString("\n")}                       
                    """.trimIndent())
                },
        {
        it.printStackTrace()
    }
    )
}