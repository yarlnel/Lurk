package com.bylinsoftware.lurk.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun getGsonBuilder(): Gson = GsonBuilder().setPrettyPrinting().create()