package com.example.trelloclone.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityCardDetailBinding
import com.example.trelloclone.dialog.ColorDialog
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.Card
import com.example.trelloclone.utils.Constant

class CardDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailBinding
    private var mPositionCard:Int = -1
    private var mPositionTask:Int = -1
    private var mBoard:Board? = null
    private var mUpdateCard: Card? = null
    private var mCloneCard: Card? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mPositionCard = intent.getIntExtra(Constant.POSITION_CARD,-1)
        mPositionTask = intent.getIntExtra(Constant.POSITION_TASK,-1)
        mBoard = intent.getParcelableExtra(Constant.OBJECT_BOARD)
        mBoard!!.taskList.removeAt(mBoard!!.taskList.size - 1)
        mUpdateCard = mBoard!!.taskList[mPositionTask].cardList[mPositionCard]
        mCloneCard = Card(createBy = mUpdateCard!!.createBy, assignedTo = mUpdateCard!!.assignedTo, selectedColor = mUpdateCard!!.selectedColor)
        setToolbar()
        setupVisualizationData()


        binding.btnUpdate.setOnClickListener {
            setupUpdate()
        }
        binding.tvSelectColor.setOnClickListener {
            val dialog =object: ColorDialog(this,Constant.colorsList(),mBoard!!.taskList[mPositionTask].cardList[mPositionCard].selectedColor,"Select label color"){
                override fun onItemSelected(color: String) {
                    this@CardDetailActivity.binding.tvSelectColor.text = ""
                    mCloneCard!!.selectedColor = color
                    this@CardDetailActivity.binding.tvSelectColor.setBackgroundColor(Color.parseColor(color))
                }
            }
            dialog.show()
        }
    }

    private fun setupVisualizationData() {
        binding.etNameCard.setText(mUpdateCard!!.name)
        binding.etNameCard.setSelection(mUpdateCard!!.name.length)
        if(mUpdateCard!!.selectedColor.isNotEmpty()){
            binding.tvSelectColor.text = "";
            binding.tvSelectColor.setBackgroundColor(Color.parseColor(mUpdateCard!!.selectedColor))
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        supportActionBar!!.title = mUpdateCard!!.name
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupUpdate() {
        val name = binding.etNameCard.text.toString().trim()
        if(name.isNotEmpty()){
            var mChange = false
            this.showProgressDialog(resources.getString(R.string.please_wait))
            if(mUpdateCard!!.name != name){
                mUpdateCard!!.name = name
                mChange = true
            }
            if(mUpdateCard!!.selectedColor != mCloneCard!!.selectedColor){
                mUpdateCard!!.selectedColor = mCloneCard!!.selectedColor
                mChange = true
            }
            mBoard!!.taskList[mPositionTask].cardList[mPositionCard] = mUpdateCard!!
            if(mChange) FirestoreClass().updateListInBoard(this, hashMapOf(Constant.KEY_TASK_LIST to mBoard!!.taskList), mBoard!!.DocumentId)
            else finish()
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