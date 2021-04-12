package com.bylinsoftware.lurk.models

import androidx.room.Entity

@Entity(tableName = "article_element")
interface ArticleElement {
    val content: String
}

@Entity(tableName = "plashka")
class Plashka (
    val imgUrl: String,
    val title: String,
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "li")
class Li(
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "p")
class P(
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "quote")
class Quote(
    override val content: String,
    val authorHrefs: List<Pair<String, String>>,
    val listOfHrefs: List<Pair<String, String>>,
    val author: String
) : ArticleElement

@Entity(tableName = "quote_tiny")
class QuoteTiny(
    override val content: String,
    val authorHrefs: List<Pair<String, String>>,
    val listOfHrefs: List<Pair<String, String>>,
    val author: String
) : ArticleElement

@Entity(tableName = "quote_no_name")
class QuoteNoName(
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "img")
class Img(
    val imgUrl: String,
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>,
    val highResolutionImageUrl: String
) : ArticleElement

@Entity(tableName = "h3")
class H3(
    override val content: String,
    val id: String
) : ArticleElement

@Entity(tableName = "h2")
class H2(
    override val content: String,
    val id: String
) : ArticleElement

@Entity(tableName = "toc_title")
class TocTitle(
    val title: String,
    override val content: String
) : ArticleElement

@Entity(tableName = "toc_li")
class TocLi(
    val id: String,
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "mini_li")
class MiniLi(
    val id: String,
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "video_box")
class VideoBox(
    val videoImg: String,
    val videoUrl: String,
    override val content: String,
    val listOfHrefs: List<Pair<String, String>>
) : ArticleElement

@Entity(tableName = "code_box")
class CodeBox(
    val text: String,
    val colorData: List<Pair<String, String>>,
    override val content: String
) : ArticleElement
