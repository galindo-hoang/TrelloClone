package com.example.trelloclone.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloclone.databinding.ItemCardBinding
import com.example.trelloclone.models.Card

class CardAdapter(private val list: ArrayList<Card>):RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    class CardViewHolder(itemView: ItemCardBinding): RecyclerView.ViewHolder(itemView.root){
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvName.text = model.name
        if(model.selectedColor != ""){
            holder.binding.vLabel.setBackgroundColor(Color.parseColor(model.selectedColor));
            holder.binding.vLabel.visibility = View.VISIBLE;
        }
        holder.binding.cvCard.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position,model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private var onClickListener: OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int,model: Card)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}