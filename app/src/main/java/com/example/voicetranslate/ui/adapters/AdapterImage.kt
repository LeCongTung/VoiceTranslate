package com.example.voicetranslate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Image
import kotlinx.android.synthetic.main.item_gallery.view.*


class AdapterImage(var listener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterImage.MyViewHolder>() {

    var imageList = emptyList<Image>()
    var quantity = ArrayList<Image>()
    var checkColor = false
    var isChoose = false

    interface OnItemClickListener {

        fun onItemClick(image: Image)
        fun onLongClick(image: Image)
        fun onUnDeleteClick(image: Image)
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

        if (checkColor){
            holder.itemView.item.setBackgroundResource(R.color.longPress)
            holder.itemView.btn_check.visibility = View.VISIBLE
            holder.itemView.btn_show.visibility = View.GONE
        }else{
            holder.itemView.item.setBackgroundResource(R.color.white)
            holder.itemView.btn_check.visibility = View.GONE
            holder.itemView.btn_show.visibility = View.VISIBLE
        }

        holder.itemView.item.setOnLongClickListener {
            if (quantity.size == 0){
                quantity.add(currentItem)
                holder.itemView.item.setBackgroundResource(R.color.longPress)
                holder.itemView.btn_check.visibility = View.VISIBLE
                holder.itemView.btn_show.visibility = View.GONE

                listener.onLongClick(currentItem)
            }
            Log.d("abc", "${quantity.size} & $currentItem")
            true
        }

        holder.itemView.item.setOnClickListener {
            if (isChoose){
                if (!quantity.contains(currentItem)){
                    holder.itemView.item.setBackgroundResource(R.color.longPress)
                    holder.itemView.btn_check.visibility = View.VISIBLE
                    holder.itemView.btn_show.visibility = View.GONE

                    quantity.add(currentItem)
                }
                else{
                    holder.itemView.item.setBackgroundResource(R.color.white)
                    holder.itemView.btn_check.visibility = View.GONE
                    holder.itemView.btn_show.visibility = View.VISIBLE

                    quantity.remove(currentItem)
                }
                listener.onUnDeleteClick(currentItem)
            }
            Log.d("abc", "${quantity.size} & $currentItem")
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun setData(image: List<Image>) {
        this.imageList = image
        notifyDataSetChanged()
    }

    fun selectedAll(){
        quantity.clear()
        quantity.addAll(imageList)
        notifyDataSetChanged()
    }

    fun clearSelectedAll(){
        quantity.clear()
        notifyDataSetChanged()
    }
}