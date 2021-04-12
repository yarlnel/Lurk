package com.bylinsoftware.lurk.models

data class SearchPage (
    val titleMatches: List<Pair<String, String>>,
    val textMatches: List<Pair<String, String>>
)