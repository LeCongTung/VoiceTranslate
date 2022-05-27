package com.example.voicetranslate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Language

class AdapterLanguage(private var list: ArrayList<Language>): RecyclerView.Adapter<AdapterLanguage.MyViewHolder>() {

    //    Choice a product in type
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = list[position]
        holder.itemtitle.text = currentitem.language
        holder.itemimage.setImageResource(currentitem.image!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder (itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val itemtitle: TextView = itemView.findViewById(R.id.value)
        val itemimage: ImageView = itemView.findViewById(R.id.flagimage)
        val sign: ImageView = itemView.findViewById(R.id.sign_tick)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun filterList(filteredNames: ArrayList<Language>) {

        this.list = filteredNames
        notifyDataSetChanged()
    }
}