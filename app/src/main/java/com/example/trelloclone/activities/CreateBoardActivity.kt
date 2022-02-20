package com.example.trelloclone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityCreateBoardBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.*
import com.example.trelloclone.utils.Constant
import com.google.firebase.storage.FirebaseStorage

class CreateBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var loadImageFromGallery: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var user:User
    private var imageUri:Uri? = null
    private var imageUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = intent.getParcelableExtra(Constant.OBJECT_USER)!!
        setToolbar()
        setupLoadImageFromGallery()


        binding.civImageBoard.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                loadImageFromGallery.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constant.READ_PERMISSION_CODE)
            }
        }

        binding.btnCreate.setOnClickListener {
            uploadImageToFirestoreUrl()
        }
    }

    private fun uploadImageToFirestoreUrl() {
        this.showProgressDialog(resources.getString(R.string.please_wait))
        if(imageUri != null){
            val sRef = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE" + System.currentTimeMillis() + "." + Constant.getExternalFile(imageUri!!,this))
            sRef.putFile(imageUri!!).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                    imageUrl = url.toString()
                    doCreateBoard()
                }
            }
        }else{
            doCreateBoard()
        }
    }

    private fun doCreateBoard() {
        val name = binding.etBoardName.text.toString()
        if(name.isNotEmpty()){
            val board = Board(Name = name, Image = imageUrl, CreatedBy = this.user.Name, AssignedTo = arrayListOf(this.user.Id))
            FirestoreClass().createBoard(this,board)
        }else{
            this.hideProgressDialog()
            Toast.makeText(this,"Please fill name of board",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constant.READ_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadImageFromGallery.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }else{
                Toast.makeText(this,"you denied permission access gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLoadImageFromGallery(){
        this.loadImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                if(data != null){
                    try {
                        imageUri = data.data
                        Glide
                            .with(this)
                            .load(data.data)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(binding.civImageBoard)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back_ios_24)
        }
        binding.tb.title = "Create Board"
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun finishCreateBoard(){
        this.hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}