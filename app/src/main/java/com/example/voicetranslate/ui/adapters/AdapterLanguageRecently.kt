package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Saved
import kotlinx.android.synthetic.main.item_language.view.*

class AdapterLanguageRecently(var listener: onItemClickListener): RecyclerView.Adapter<AdapterLanguageRecently.MyViewHolder>() {

//    Choice a product in type
    var recentlyList = emptyList<Saved>()
    var selected: String = ""

    interface onItemClickListener {

        fun onItemClick(language: Saved)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recentlyList[position]
        holder.itemView.value.text = currentItem.language
        Glide.with(holder.itemView.context).load(currentItem.image).into(holder.itemView.flagimage)

        holder.itemView.item.setOnClickListener {
            listener.onItemClick(currentItem)
        }

        if (selected == currentItem.id)
            holder.itemView.display_used.visibility = View.VISIBLE
        else
            holder.itemView.display_used.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return recentlyList.size
    }

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    fun setData(data: List<Saved>) {
        this.recentlyList = data
        notifyDataSetChanged()
    }
}