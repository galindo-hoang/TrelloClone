package com.example.trelloclone.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloclone.databinding.ItemColorBinding

class ItemColorAdapter(val list: ArrayList<String>, private val checked: String):RecyclerView.Adapter<ItemColorAdapter.ItemColorViewHolder>() {
    class ItemColorViewHolder(itemView: ItemColorBinding):RecyclerView.ViewHolder(itemView.root){
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemColorViewHolder {
        return ItemColorViewHolder(ItemColorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemColorViewHolder, position: Int) {
        val model = list[position]
        Log.e("----",checked)
        if(model == checked){
            holder.binding.ivChecked.visibility = View.VISIBLE
        }else{
            holder.binding.ivChecked.visibility = View.GONE
        }
        holder.binding.vColor.setBackgroundColor(Color.parseColor(model))
        holder.binding.fl.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(selected: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private var onClickListener:OnClickListener? = null

}