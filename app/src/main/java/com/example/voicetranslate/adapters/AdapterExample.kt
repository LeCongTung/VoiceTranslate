package com.example.voicetranslate.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.models.DataItem

class AdapterExample(val context: Context, val list: List<DataItem>): RecyclerView.Adapter<AdapterExample.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterExample.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)
        return AdapterExample.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterExample.MyViewHolder, position: Int) {

        val currentitem = list[position]
        holder.itemexample.text = currentitem.title
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        val itemexample: TextView = itemView.findViewById(R.id.value)
    }
}