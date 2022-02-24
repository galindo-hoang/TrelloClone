package com.example.trelloclone.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityCardDetailBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.utils.Constant

class CardDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailBinding
    private var mPositionCard:Int = -1
    private var mPositionTask:Int = -1
    private var mBoard:Board? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPositionCard = intent.getIntExtra(Constant.POSITION_CARD,-1)
        mPositionTask = intent.getIntExtra(Constant.POSITION_TASK,-1)
        mBoard = intent.getParcelableExtra(Constant.OBJECT_BOARD)

        setToolbar()
        setupVisualizationData()
        binding.btnUpdate.setOnClickListener {
            setupUpdate()
        }
    }

    private fun setupVisualizationData() {
        binding.etNameCard.setText(mBoard!!.taskList[mPositionTask].cardList[mPositionCard].name)
        binding.etNameCard.setSelection(binding.etNameCard.text.toString().length)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        supportActionBar!!.title = mBoard!!.taskList[mPositionTask].cardList[mPositionCard].name
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupUpdate() {
        val name = binding.etNameCard.text.toString().trim()
        if(name.isNotEmpty()){
            if(name != mBoard!!.taskList[mPositionTask].cardList[mPositionCard].name){
                this.showProgressDialog(resources.getString(R.string.please_wait))
                mBoard!!.taskList[mPositionTask].cardList[mPositionCard].name = name
                FirestoreClass().updateListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mBoard!!.taskList), mBoard!!.DocumentId)
            }
        }else{
            Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_card,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card -> {
                showAlertDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog
            .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_warning_24, null))
            .setMessage("Do you want to delete card ${mBoard!!.taskList[mPositionTask].cardList[mPositionCard].name}")
            .setTitle("Warning")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                this.showProgressDialog(resources.getString(R.string.please_wait))
                mBoard!!.taskList[mPositionTask].cardList.removeAt(mPositionCard)
                FirestoreClass().updateListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mBoard!!.taskList), mBoard!!.DocumentId)
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        alertDialog.show()
    }

    fun successUpdateNameOfCardInList() {
        this.hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}