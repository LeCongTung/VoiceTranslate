package com.example.voicetranslate.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.models.DataItem

class AdapterExample(val context: Context, var list: List<DataItem>): RecyclerView.Adapter<AdapterExample.MyViewHolder>() {

    //    Choice a product in type
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterExample.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)
        return AdapterExample.MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: AdapterExample.MyViewHolder, position: Int) {

        val currentitem = list[position]
        holder.itemexample.text = currentitem.title
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder (itemView: View, listener: AdapterExample.onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val itemexample: TextView = itemView.findViewById(R.id.value)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun filterList(filteredNames: ArrayList<DataItem> ) {
        this.list = filteredNames
        notifyDataSetChanged()
    }
}