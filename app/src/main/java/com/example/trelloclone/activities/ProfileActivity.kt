package com.example.trelloclone.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityProfileBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class ProfileActivity : BaseActivity() {
    private lateinit var user: User
    private lateinit var loadImageFromGallery: androidx.activity.result.ActivityResultLauncher<Intent>
    private var haveChange = false
    private var dataHash = hashMapOf<String,Any>()
    private lateinit var binding:ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        FirestoreClass().retrieveUser(this)
        setupLoadImageFromGallery()
        binding.civProfile.setOnClickListener {
            setupLoadImage()
        }

        binding.btnUpdate.setOnClickListener {
            this.showProgressDialog("Waiting update...")
            uploadImageToStorage()
        }
    }

    private fun setupUpdateUser() {
        val name = binding.etName.text.toString()
        if(name.isNotEmpty()){
            val number = when(binding.etMobile.text.toString()){
                "" -> 0
                else -> binding.etMobile.text.toString().toLong()
            }

            if(name != user.Name){
                haveChange = true
                dataHash[Constant.KEY_NAME] = name
            }

            if(number != user.Mobile){
                haveChange = true
                dataHash[Constant.KEY_MOBILE] = number
            }
            if(haveChange){
                FirestoreClass().updateUser(this,dataHash)
            }
        }else{
            this.hideProgressDialog()
            Toast.makeText(this,"Please fill name",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLoadImage() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            loadImageFromGallery.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constant.READ_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constant.READ_PERMISSION_CODE ){
            if(grantResults.isNotEmpty() &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadImageFromGallery.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }else{
                Toast.makeText(this,"you denied permission access gallery",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back_ios_24)
        }
        binding.tb.title = "My Profile"
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupLoadImageFromGallery(){
        loadImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if(data!=null){
                    try{
                        dataHash[Constant.KEY_IMAGE] = data.data.toString()
                        Glide
                            .with(this)
                            .load(data.data)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(binding.civProfile)
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load image from camera", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }

    private fun uploadImageToStorage(){
        if(this.dataHash.containsKey(Constant.KEY_IMAGE)){
            val sRef = FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+Constant.getExternalFile(Uri.parse(this.dataHash[Constant.KEY_IMAGE].toString()),this))
            sRef.putFile(Uri.parse(this.dataHash[Constant.KEY_IMAGE].toString())).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                    this.dataHash[Constant.KEY_IMAGE] = url.toString()
                    this@ProfileActivity.haveChange = true
                    setupUpdateUser()
                }
            }
        }else{
            setupUpdateUser()
        }
    }


    fun successRetrieveUser(objects: User) {
        user = objects
        binding.etName.setText(objects.Name)
        binding.etEmail.setText(objects.Email)
        if(objects.Mobile != 0L) binding.etMobile.setText(objects.Mobile.toString())

        Glide
            .with(this)
            .load(objects.Image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.civProfile)
    }

    fun setupUpdate() {
        this.hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}