package com.bylinsoftware.lurk.adapters

import android.app.Activity
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bylinsoftware.lurk.R
import com.bylinsoftware.lurk.MainActivity
import com.bylinsoftware.lurk.db.history.History
import com.bylinsoftware.lurk.utils.makeSpannable


class HistoryAdapter (private val elements: List<History>, private val activity: Activity)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val tv: TextView = itemView.findViewById(R.id.tv_p)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_p, parent, false))
    override fun getItemCount() = elements.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val history = elements[position]
            holder.tv . text = makeSpannable(history.title, listOf(history.title to history.href)) { _, href ->
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("uri", href)
            intent.putExtra("scroll_y", history.y)
            activity.startActivity(intent)
            activity.finish()
        }
        holder.tv . movementMethod = LinkMovementMethod . getInstance ()
    }
}