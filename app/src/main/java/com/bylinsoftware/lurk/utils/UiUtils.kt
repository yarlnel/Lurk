package com.bylinsoftware.lurk.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.bylinsoftware.lurk.MainActivity
import com.bylinsoftware.lurk.gson.getGsonBuilder

fun List<View>.makeInvisible()
{
    this.forEach { view ->
        view.visibility = View.INVISIBLE
    }
}

fun List<View>.makeVisible()
{
    this.forEach { view ->
        view.visibility = View.VISIBLE
    }
}

fun List<View>.makeListOfViewVisibleAndClickable()
{
    this.makeVisible()
    this.makeClickable()
}

fun List<View>.makeListOfViewInvisibleAndUnClickable()
{
    this.makeInvisible()
    this.makeUnClickable()
}

fun List<View>.makeUnClickable()
{
    this.forEach { view ->
        view.isClickable = false
    }
}

fun List<View>.makeClickable()
{
    this.forEach { view ->
        view.isClickable = true
    }
}

fun ProgressBar.visible()
{
    this.visibility = ProgressBar.VISIBLE
}

fun ProgressBar.invisible()
{
    this.visibility = ProgressBar.INVISIBLE
}

fun Any.puts(context: Context)
{
    Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
}


fun Activity.goToRef(path: String)
{
    if (path.contains(com.bylinsoftware.lurk.dataSource.url)) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uri", path)
        startActivity(intent)
    } else {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(path))
        startActivity(intent)
    }
}


fun makeSpannable(text: String, listOfHrefs: List<Pair<String, String>>, lambda: (text: String, href: String) -> Unit) : SpannableString
{
    val spannable = SpannableString(text)
    Log.e("span", getGsonBuilder().toJson(listOfHrefs))
    listOfHrefs.forEach { e ->
        if (e.first in text && e.first.isNotEmpty()) {
            val indexStart = text.indexOf(e.first)
            val indexEnd = indexStart + e.first.length
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Log.e("span", "${e.first} :: ${e.second}")
                   lambda(e.first, e.second)
                }
            }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannable
}

fun Activity.makeReference(text: String, listOfHrefs: List<Pair<String, String>>) : SpannableString
    = makeSpannable(text = text, listOfHrefs = listOfHrefs) { _, ref ->
        this.goToRef(ref)
    }

