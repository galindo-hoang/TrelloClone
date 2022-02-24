package com.example.trelloclone.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapter.MemberAdapter
import com.example.trelloclone.databinding.ActivityMembersBinding
import com.example.trelloclone.databinding.DialogSearchMemberBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoard: Board
    private lateinit var mArrayListUser: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mBoard = intent.getParcelableExtra(Constant.OBJECT_BOARD)!!
        setupShowMembers()
        setToolbar()
    }

    private fun setupShowMembers() {
        this.showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().fetchUserListInBoard(this,mBoard.DocumentId)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        supportActionBar!!.title = "Members"
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {
                showDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showDialog(){
        val dialog = Dialog(this)
        val dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {
            dialog.dismiss()
            val email = dialogBinding.etEmailSearchMember.text.toString().trim()
            dialogBinding.etEmailSearchMember.text!!.clear()
            if(email.isNotEmpty()){
                val alertDialog = AlertDialog.Builder(this)
                alertDialog
                    .setTitle("Notification")
                    .setMessage("Do you want to add $email")
                    .setPositiveButton("Add") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        this@MembersActivity.showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().fetchUserByEmail(this@MembersActivity, email)
                    }
                    .setNegativeButton("Cancel"){
                        dialogInterface,_ ->
                        dialogInterface.dismiss()
                    }
                alertDialog.show()
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun successFetchUserByEmail(user: User?) {
        val list = mBoard.AssignedTo
        list.add(user!!.Id)
        FirestoreClass().updateMembersInBoard(this,hashMapOf(Constant.KEY_ASSIGNED_TO to list),mBoard.DocumentId)
    }

    fun successUpdateMembersInBoard() {
        this.hideProgressDialog()
        this.setupShowMembers()
    }

    fun successFetchUserListInBoard(listUser: ArrayList<String>) {
        mBoard.AssignedTo = listUser
        this.hideProgressDialog()
        FirestoreClass().fetchUserByBoard(this,mBoard.AssignedTo)
    }

    fun successFetchUserByBoard(usersList: java.util.ArrayList<User>) {
        this.hideProgressDialog()
        mArrayListUser = usersList
        binding.rcvMemberList.adapter = MemberAdapter(mArrayListUser,this)
        binding.rcvMemberList.layoutManager = LinearLayoutManager(this)
    }
}