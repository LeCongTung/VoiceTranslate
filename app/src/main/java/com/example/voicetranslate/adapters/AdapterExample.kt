package com.example.voicetranslate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Example

class AdapterExample(private var list: ArrayList<Example>): RecyclerView.Adapter<AdapterExample.MyViewHolder>() {

    //    Choice a product in type
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = list[position]
        holder.itemtitle.text = currentitem.example
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder (itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val itemtitle: TextView = itemView.findViewById(R.id.value)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun filterList(filteredNames: ArrayList<Example>) {

        this.list = filteredNames
        notifyDataSetChanged()
    }
}