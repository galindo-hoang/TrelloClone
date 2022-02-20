package com.example.trelloclone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ItemBoardBinding
import com.example.trelloclone.models.Board

class BoardAdapter(private val list:ArrayList<Board>):RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private var onClickListener: OnClickListener? = null
    interface OnClickListener{
        fun onClick(position: Int,model: Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }



    class BoardViewHolder(itemView: ItemBoardBinding):RecyclerView.ViewHolder(itemView.root){
        val boardBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val data = list[position]
        holder.boardBinding.tvCreatedBy.text = data.CreatedBy
        holder.boardBinding.tvName.text = data.Name
        Glide
            .with(holder.itemView)
            .load(data.Image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.boardBinding.civBoardImage)
        holder.boardBinding.llItemBoard.setOnClickListener {
            if(this.onClickListener != null) {
                this.onClickListener!!.onClick(position,data)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}