package com.example.trelloclone.activities

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapter.ListAdapter
import com.example.trelloclone.databinding.ActivityTaskListBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.*
import com.example.trelloclone.utils.Constant
import java.text.FieldPosition

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
        binding.rcvTaskList.adapter = ListAdapter(mTaskList,{ editText -> addList(editText) },{ nameList, position -> editTitleList(nameList, position) }, { editText -> addCard(editText) })
        binding.rcvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun addList(editText: EditText){
        this.showProgressDialog(resources.getString(R.string.please_wait))
        val name = editText.text.toString().trim()
        if(name.isNotEmpty()){
            val data = mBoard.taskList
            data.add(0,Task(Title = name, createBy = mUser.Name))
            data.removeAt(data.size - 1)
            FirestoreClass().addListIntoBoard(this,hashMapOf(Constant.KEY_TASK_LIST to data),mBoard.DocumentId)
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

    private fun addCard(editText: EditText){

    }

    fun successAddListIntoBoard() {
        this.hideProgressDialog()
        setupShowList()
        mTaskList.add(Task())
    }

    fun successEditListInBoard() {
        this.hideProgressDialog()
        mTaskList.add(Task())
    }
}