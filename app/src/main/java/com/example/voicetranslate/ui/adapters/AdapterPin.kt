package com.example.voicetranslate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Pin
import kotlinx.android.synthetic.main.item_gallery.view.*

class AdapterPin(var listener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterPin.MyViewHolder>() {

    var pinList = emptyList<Pin>()
    private var quantity: Int = 0

    interface OnItemClickListener {

        fun onItemClickPin(pin: Pin)
        fun onLongClickPin(time: String)
        fun onUnDeleteClickPin(time: String)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = pinList[position]
        holder.itemView.toLang.text = "${currentItem.fromLang} to ${currentItem.toLang}"
        holder.itemView.tv_timeSave.text = currentItem.time
        Glide.with(holder.itemView.context).load(currentItem.pathImage).into(holder.itemView.item_photo);
        holder.itemView.btn_show.setOnClickListener {
            listener.onItemClickPin(currentItem)
        }

        holder.itemView.item.setOnLongClickListener {
            if (quantity == 0){
                holder.itemView.item.setBackgroundResource(R.color.longPress)
                holder.itemView.btn_check.visibility = View.VISIBLE
                holder.itemView.btn_show.visibility = View.GONE

                quantity++
                listener.onLongClickPin(currentItem.time)
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
                listener.onUnDeleteClickPin(currentItem.time)
            }
        }
    }

    override fun getItemCount(): Int {
        return pinList.size
    }

    fun setData(pin: List<Pin>) {
        this.pinList = pin
        notifyDataSetChanged()
    }
}