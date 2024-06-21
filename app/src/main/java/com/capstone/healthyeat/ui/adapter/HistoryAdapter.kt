package com.capstone.healthyeat.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.healthyeat.R
import com.capstone.healthyeat.data.remote.response.PayloadItem
import com.capstone.healthyeat.ui.view.detail.DetailActivity

class HistoryAdapter (private val listHistory: List<PayloadItem>) : RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.imageViewHistory)
        val tvName: TextView = itemView.findViewById(R.id.fruitHistoryText)
        val tvDate: TextView = itemView.findViewById(R.id.dateHistoryText)
        val tvCondition: TextView = itemView.findViewById(R.id.conditionHistoryText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (condition, informationName,createdDate,percentage,userEmail,id,urlImage) = listHistory[position]
        Glide.with(holder.itemView.context)
            .load(urlImage)
            .into(holder.imgPhoto)
        holder.tvName.text = informationName
        holder.tvDate.text = createdDate
        if(condition.equals("Good")){
            holder.tvCondition.text = condition
            holder.tvCondition.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange))
        }else{
            holder.tvCondition.text = condition
            holder.tvCondition.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grey))
        }
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentDetail.putExtra("id", id.toString())
            intentDetail.putExtra("informationName", informationName)
            intentDetail.putExtra("previousPage", "History")
            holder.itemView.context.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }
}