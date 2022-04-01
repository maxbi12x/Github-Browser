package com.example.githubbrowser.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubbrowser.R
import com.example.githubbrowser.responsemodels.IssueModel
import kotlinx.android.synthetic.main.issue_list_item.view.*

open class IssueAdapter(
    private val context: Context,
    private var list: ArrayList<IssueModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                //todo create layout
                R.layout.issue_list_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.issueTv.text = model.title
            holder.itemView.tvIssueCreator.text = model.user.login
            Glide.with(context).load(model.user.avatar_url).into(holder.itemView.ivAvatar)
            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: IssueModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}