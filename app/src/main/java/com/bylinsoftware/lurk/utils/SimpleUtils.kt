package com.bylinsoftware.lurk.utils

import com.bylinsoftware.lurk.gson.getGsonBuilder

private const val url = "https://lurkmore.to"

public fun String.makeReference() : String
{
    return if (this.contains("http")) {
        this
    } else {
        url+this
    }
}

public fun String.clearAllSymbols (str: String): String {
    var result = this
    str.split(" ").forEach { e ->
        result = result.replace(e, "")
    }
    return result
}



public fun String.makeCssStyleMap(): Map<String, String> {
    return this
        
        .clearAllSymbols("{ }")
        .split(";")
        .filter { it != ""}
        .map { e -> e.split(":")[0].replace(" ", "") to e.split(":")[1].replace(" ", "") }
        .toMap()
}




fun String.pln() {
    println(this)
}

// test our utils
fun main() {
    getGsonBuilder().toJson("fgsdfgsdfsdf {color: #ff7700;font-weight: bold}".makeCssStyleMap()).pln()
}