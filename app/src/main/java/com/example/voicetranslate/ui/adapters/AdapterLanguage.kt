package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Language
import kotlinx.android.synthetic.main.item_language.view.*

class AdapterLanguage(var listener: onItemClickListener, private var languageList: ArrayList<Language>): RecyclerView.Adapter<AdapterLanguage.MyViewHolder>() {

    var selected: String = ""

    interface onItemClickListener {

        fun onItemClick(language: Language)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = languageList[position]
        holder.itemView.value.text = currentItem.language
        Glide.with(holder.itemView.context).load(currentItem.image).into(holder.itemView.flagimage)

        holder.itemView.isNestedScrollingEnabled = false

        if (selected == currentItem.language)
            holder.itemView.display_used.visibility = View.VISIBLE
        else
            holder.itemView.display_used.visibility = View.INVISIBLE

        holder.itemView.item.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {}
}