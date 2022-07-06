package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Topic

class AdapterTopic(private var list: ArrayList<Topic>): RecyclerView.Adapter<AdapterTopic.MyViewHolder>() {

    //    Choice a product in type
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = list[position]
        holder.itemtitle.text = currentitem.title
        holder.itemimage.setImageResource(currentitem.image!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder (itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val itemtitle: TextView = itemView.findViewById(R.id.item_title)
        val itemimage: ImageView = itemView.findViewById(R.id.item_logo)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun filterList(filteredNames: ArrayList<Topic>) {

        this.list = filteredNames
        notifyDataSetChanged()
    }
}