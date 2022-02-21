package com.example.trelloclone.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloclone.R
import com.example.trelloclone.adapter.CardAdapter
import com.example.trelloclone.adapter.ListAdapter
import com.example.trelloclone.databinding.ActivityTaskListBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.*
import com.example.trelloclone.utils.Constant

class TaskListActivity : BaseActivity() {
    private lateinit var binding:ActivityTaskListBinding
    private lateinit var mBoard: Board
    private lateinit var mUser: User
    private lateinit var mTaskList: ArrayList<Task>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mBoard = intent.getParcelableExtra(Constant.OBJECT_BOARD)!!
        mUser = intent.getParcelableExtra(Constant.OBJECT_USER)!!
        setToolbar()
        setupShowList()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        supportActionBar!!.title = mBoard.Name
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupShowList() {
        this.showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().fetchListInBoard(this,mBoard)
    }

    fun successFetchListInBoard(taskList: ArrayList<Task>) {
        this.hideProgressDialog()
        mTaskList = taskList
        mTaskList.add(Task())
        binding.rcvTaskList.adapter = ListAdapter(
            mTaskList,
            { editText -> addList(editText) },
            { nameList, position -> editTitleList(nameList, position) },
            { editText,position -> addCard(editText,position) },
            { position -> deleteList(position)},
            { recyclerViewCard, cardList -> setupRecyclerViewCard(recyclerViewCard,cardList)}
        )
        binding.rcvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun setupRecyclerViewCard(recyclerViewCard: RecyclerView, cardList: ArrayList<Card>) {
        recyclerViewCard.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerViewCard.adapter = CardAdapter(cardList)
    }

    private fun addList(editText: EditText){
        this.showProgressDialog(resources.getString(R.string.please_wait))
        val name = editText.text.toString().trim()
        if(name.isNotEmpty()){
            mTaskList.add(0,Task(Title = name, createBy = mUser.Name))
            mTaskList.removeAt(mTaskList.size - 1)
            FirestoreClass().updateListInBoard(this,hashMapOf(Constant.KEY_TASK_LIST to mTaskList),mBoard.DocumentId)
        }else{
            this.hideProgressDialog()
            Toast.makeText(this,"Please Enter Name",Toast.LENGTH_SHORT).show()
        }
    }

    private fun editTitleList(nameList: String,position: Int){
        if(nameList.isEmpty()){
            Toast.makeText(this,"Please Enter Name", Toast.LENGTH_SHORT).show()
        }else{
            this.showProgressDialog(resources.getString(R.string.please_wait))
            mTaskList[position].Title = nameList
            mTaskList.removeAt(mTaskList.size - 1)
            FirestoreClass().editTitleListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mTaskList),mBoard.DocumentId)
        }
    }

    private fun deleteList(position: Int){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog
            .setTitle("DELETE")
            .setMessage("Do you want to delete ${mTaskList[position].Title} List ?")
            .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_warning_24, null))
            .setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                this.showProgressDialog(resources.getString(R.string.please_wait))
                mTaskList.removeAt(position)
                mTaskList.removeAt(mTaskList.size - 1)
                FirestoreClass().updateListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mTaskList), mBoard.DocumentId)
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        val dialog = alertDialog.create()
        dialog.show()
    }

    private fun addCard(editText: EditText,position: Int){
        val name = editText.text.toString().trim()
        if(name.isNotEmpty()){
            this.showProgressDialog(resources.getString(R.string.please_wait))
            mTaskList[position].cardList.add(Card(name = name, createBy = mUser.Id))
            mTaskList.removeAt(mTaskList.size - 1)
            FirestoreClass().updateListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mTaskList),mBoard.DocumentId)
        }else{
            Toast.makeText(this,"Please enter name card",Toast.LENGTH_SHORT).show()
        }
    }

    fun successUpdateListInBoard() {
        this.hideProgressDialog()
        setupShowList()
        mTaskList.add(Task())
    }

    fun successEditListInBoard() {
        this.hideProgressDialog()
        mTaskList.add(Task())
    }
}