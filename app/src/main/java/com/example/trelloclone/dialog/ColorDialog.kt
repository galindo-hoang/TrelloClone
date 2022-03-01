package com.example.trelloclone.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.adapter.ItemColorAdapter
import com.example.trelloclone.databinding.ColorDialogBinding

abstract class ColorDialog(
    context: Context,
    private val listColor: ArrayList<String>,
    private val selectedColor: String = "",
    val title: String
):Dialog(context) {
    val binding = ColorDialogBinding.inflate(LayoutInflater.from(context))
    override fun onCreate(savedInstanceState: Bundle?) {
//        val view = LayoutInflater.from(context).inflate(R.layout.color_dialog,null)
        setContentView(binding.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.tvTitle.text = title
        binding.rcvList.layoutManager = LinearLayoutManager(context)
        val adapter = ItemColorAdapter(listColor,selectedColor)
        adapter.setOnClickListener(object: ItemColorAdapter.OnClickListener{
            override fun onClick(selected: String) {
                dismiss()
                onItemSelected(selected)
            }
        })
        binding.rcvList.adapter = adapter
    }
    protected abstract fun onItemSelected(color: String)
}