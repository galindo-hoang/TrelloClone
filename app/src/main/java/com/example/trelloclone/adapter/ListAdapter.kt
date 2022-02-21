package com.example.trelloclone.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloclone.databinding.ItemTaskBinding
import com.example.trelloclone.models.Card
import com.example.trelloclone.models.Task

class ListAdapter(
    private val list: ArrayList<Task>,
    private val addList:(EditText) -> Unit,
    private val editTitleList:(String,Int) -> Unit,
    private val addCard:(EditText,Int) -> Unit,
    private val deleteList:(Int) -> Unit,
    private val setupCard:(RecyclerView,ArrayList<Card>) -> Unit
): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: ItemTaskBinding): RecyclerView.ViewHolder(itemView.root){
        var binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.root.layoutParams = layoutParams
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = list[position]
        if(position == list.size - 1){
            holder.binding.tvAddList.visibility = View.VISIBLE
            holder.binding.llTaskItem.visibility = View.GONE
        }else{
            holder.binding.tvAddList.visibility = View.GONE
            holder.binding.llTaskItem.visibility = View.VISIBLE
        }

        setupCard.invoke(holder.binding.rcvCardList,data.cardList)

        holder.binding.tvTitleList.text = data.Title

        // Add List

        holder.binding.tvAddList.setOnClickListener {
            holder.binding.tvAddList.visibility = View.GONE
            holder.binding.cvAddTaskList.visibility = View.VISIBLE
        }

        holder.binding.ibCloseAddList.setOnClickListener {
            holder.binding.cvAddTaskList.visibility = View.GONE
            holder.binding.tvAddList.visibility = View.VISIBLE
        }

        holder.binding.ibDoneAddList.setOnClickListener {
            addList.invoke(holder.binding.etAddListName)
        }

        // edit Title list name

        holder.binding.ibEditList.setOnClickListener {
            holder.binding.cvEditListName.visibility = View.VISIBLE
            holder.binding.llTitleView.visibility = View.GONE
            holder.binding.etEditListName.setText(holder.binding.tvTitleList.text)
        }

        holder.binding.ibCloseEditList.setOnClickListener {
            holder.binding.llTitleView.visibility = View.VISIBLE
            holder.binding.cvEditListName.visibility = View.GONE
        }

        holder.binding.ibDoneEditList.setOnClickListener {
            holder.binding.llTitleView.visibility = View.VISIBLE
            holder.binding.cvEditListName.visibility = View.GONE
            val nameList = holder.binding.etEditListName.text.toString().trim()
            if(nameList.isNotEmpty()){
                holder.binding.tvTitleList.text = nameList
            }
            editTitleList.invoke(nameList,position)
        }

        // Add task

        holder.binding.tvAddCard.setOnClickListener {
            holder.binding.cvAddCard.visibility = View.VISIBLE
            holder.binding.tvAddCard.visibility = View.GONE
        }

        holder.binding.ibCloseAddCard.setOnClickListener {
            holder.binding.tvAddCard.visibility = View.VISIBLE
            holder.binding.cvAddCard.visibility = View.GONE
        }

        holder.binding.ibDoneAddCard.setOnClickListener {
            addCard.invoke(holder.binding.etAddCardName,position)
        }

        holder.binding.ibDeleteList.setOnClickListener {
            deleteList(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}