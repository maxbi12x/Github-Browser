package com.example.githubbrowser.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubbrowser.R
import com.example.githubbrowser.responsemodels.CommitModel
import kotlinx.android.synthetic.main.commit_list_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class CommitAdapter(
    private val context: Context,
    private var list: ArrayList<CommitModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.commit_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            val originalFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val targetFormat: DateFormat = SimpleDateFormat("dd MMMM yy", Locale.ENGLISH)
            val date: Date = originalFormat.parse(model.commit.committer.date.split('T')[0])
            val formattedDate: String = targetFormat.format(date)
            holder.itemView.tvDate.text = formattedDate
            holder.itemView.tvMessage.text = model.commit.message
            holder.itemView.tvCommiter.text = if (model.committer.login == "web-flow") {
                Glide.with(context).load(model.author.avatar_url)
                    .into(holder.itemView.iv_commiter_avatar)
                model.author.login

            } else {
                Glide.with(context).load(model.committer.avatar_url)
                    .into(holder.itemView.iv_commiter_avatar)
                model.committer.login
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}