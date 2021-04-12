package com.bylinsoftware.lurk.utils

import android.util.Log


data class Logger (
    val tag: String = "without tag",
    val from: String,
    val doWithLog: () -> Unit = {}
)

fun Logger.log(t: Throwable)
{
    Log.e(this.tag,"""
        |subscribe error
        |from: ${this.from}
        |error text:
        |   ${t.stackTrace}
    """.trimMargin())
}

interface HasLogSystem {
    val tag: String
    val logActive: Boolean
}

public fun HasLogSystem.log(str: String) {
    if (logActive) Log.e(this.tag, str)
}

fun HasLogSystem.withLogName(str: String, func: HasLogSystem.() -> Unit) {
    val logDo = this.logActive
    object : HasLogSystem {
        override val tag = str
        override val logActive = logDo
    }.func()
}

