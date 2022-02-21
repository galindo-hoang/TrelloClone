package com.example.trelloclone.adapter

import android.view.LayoutInflater
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
    }

    override fun getItemCount(): Int {
        return list.size
    }
}