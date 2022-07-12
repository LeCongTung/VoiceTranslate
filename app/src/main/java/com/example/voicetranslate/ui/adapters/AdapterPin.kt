package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Image
import kotlinx.android.synthetic.main.item_gallery.view.*


class AdapterPin(var listener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterPin.MyViewHolder>() {

    var pinList = emptyList<Image>()
    var quantity = ArrayList<Image>()
    var checkColor = false

    interface OnItemClickListener {

        fun onItemClickPin(image: Image)
        fun onLongClickPin(image: Image)
        fun onUnDeleteClickPin(image: Image)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = pinList[position]
        holder.itemView.toLang.text = "${currentItem.fromLang} to ${currentItem.toLang}"
        holder.itemView.tv_timeSave.text = currentItem.time
        Glide.with(holder.itemView.context).load(currentItem.pathImage).into(holder.itemView.item_photo)
        holder.itemView.btn_show.setOnClickListener {
            listener.onItemClickPin(currentItem)
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

                listener.onLongClickPin(currentItem)
            }
            true
        }

        holder.itemView.item.setOnClickListener {
            if (quantity.size >0){
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
                listener.onUnDeleteClickPin(currentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return pinList.size
    }

    fun setData(pin: List<Image>) {
        this.pinList = pin
        notifyDataSetChanged()
    }

    fun selectedAll(){
        quantity.clear()
        quantity.addAll(pinList)
        notifyDataSetChanged()
    }

    fun clearSelectedAll(){
        quantity.clear()
        notifyDataSetChanged()
    }
}