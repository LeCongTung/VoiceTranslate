package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Image
import kotlinx.android.synthetic.main.item_gallery.view.*


class AdapterImage(var listener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterImage.MyViewHolder>() {

    var imageList = emptyList<Image>()
    private var quantity: Int = 0

    interface OnItemClickListener {

        fun onItemClick(image: Image)
        fun onLongClick(id: Int)
        fun onUnDeleteClick(id: Int)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = imageList[position]
        holder.itemView.toLang.text = "${currentItem.fromLang} to ${currentItem.toLang}"
        holder.itemView.tv_timeSave.text = currentItem.time
        Glide.with(holder.itemView.context).load(currentItem.pathImage).into(holder.itemView.item_photo)
        holder.itemView.btn_show.setOnClickListener {
            listener.onItemClick(currentItem)
        }

        holder.itemView.item.setOnLongClickListener {
            if (quantity == 0){
                holder.itemView.item.setBackgroundResource(R.color.longPress)
                holder.itemView.btn_check.visibility = View.VISIBLE
                holder.itemView.btn_show.visibility = View.GONE

                quantity++
                listener.onLongClick(currentItem.id)
            }
            true
        }

        holder.itemView.item.setOnClickListener {
            if (quantity >0){
                if (holder.itemView.btn_show.isVisible){
                    holder.itemView.item.setBackgroundResource(R.color.longPress)
                    holder.itemView.btn_check.visibility = View.VISIBLE
                    holder.itemView.btn_show.visibility = View.GONE

                    quantity++
                }
                else{
                    holder.itemView.item.setBackgroundResource(R.color.white)
                    holder.itemView.btn_check.visibility = View.GONE
                    holder.itemView.btn_show.visibility = View.VISIBLE

                    quantity--
                }
                listener.onUnDeleteClick(currentItem.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun setData(image: List<Image>) {
        this.imageList = image
        notifyDataSetChanged()
    }
}