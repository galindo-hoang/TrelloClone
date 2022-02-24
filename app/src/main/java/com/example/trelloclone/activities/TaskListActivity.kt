package com.example.trelloclone.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var launchCardDetailActivity: androidx.activity.result.ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mBoard = intent.getParcelableExtra(Constant.OBJECT_BOARD)!!
        mUser = intent.getParcelableExtra(Constant.OBJECT_USER)!!
        setToolbar()
        setupShowList()
        setupLaunchCardDetailActivity()
    }

    private fun setupLaunchCardDetailActivity() {
        this.launchCardDetailActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                this.setupShowList()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_members,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constant.OBJECT_BOARD,mBoard)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            { recyclerViewCard, cardList, position -> setupRecyclerViewCard(recyclerViewCard,cardList, position)}
        )
        binding.rcvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun setupRecyclerViewCard(recyclerViewCard: RecyclerView, cardList: ArrayList<Card>, positionTask: Int) {
        recyclerViewCard.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val cardAdapter = CardAdapter(cardList)
        cardAdapter.setOnClickListener(object: CardAdapter.OnClickListener{
            override fun onClick(positionCard: Int, model: Card) {
                val intent = Intent(this@TaskListActivity,CardDetailActivity::class.java)
                intent.putExtra(Constant.POSITION_CARD,positionCard)
                intent.putExtra(Constant.POSITION_TASK,positionTask)
                mBoard.taskList = mTaskList
                val cloneBoard = mBoard
                cloneBoard.taskList.removeAt(mBoard.taskList.size - 1)
                intent.putExtra(Constant.OBJECT_BOARD,cloneBoard)
                launchCardDetailActivity.launch(intent)
            }
        })
        recyclerViewCard.adapter = cardAdapter
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