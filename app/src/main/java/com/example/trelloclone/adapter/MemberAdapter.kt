package com.example.trelloclone.adapter

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MemberAdapter(private val list: ArrayList<User>, val context: Context): RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    class MemberViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val civ: CircleImageView = itemView.findViewById(R.id.civImageMember)
        val tvName: TextView = itemView.findViewById(R.id.tvNameMember)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmailMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member,parent,false))
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val data = list[position]
        Glide
            .with(context)
            .load(data.Image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.civ)
        holder.tvName.text = data.Name
        holder.tvEmail.text = data.Email
    }

    override fun getItemCount(): Int {
        return list.size
    }

}