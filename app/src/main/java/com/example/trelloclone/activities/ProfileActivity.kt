package com.example.trelloclone.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityProfileBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import java.io.IOException

class ProfileActivity : BaseActivity() {
    companion object{
        const val READ_PERMISSION_CODE = 1
    }
    private lateinit var user: User
    private lateinit var loadImageFromCamera: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var binding:ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        FirestoreClass().retrieveUser(this)
        setupLoadImageFromCamera()
        binding.civProfile.setOnClickListener {
            setupLoadImage()
        }

        binding.btnUpdate.setOnClickListener {
            setupUpdateUser()
        }
    }

    private fun setupUpdateUser() {
        val name = binding.etName.text.toString()
        if(name.isNotEmpty()){
            val data = User(
                Id = this.user.Id, Name = name, Email = this.user.Email,
                Mobile = when{
                    binding.etMobile.text.toString().isEmpty() -> 0L
                    else -> binding.etMobile.text.toString() as Long }
                , Image = this.user.Image, fcmToken = this.user.fcmToken
            )
            FirestoreClass().updateUser(this,data)
        }
    }

    private fun setupLoadImage() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            loadImageFromCamera.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),READ_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_PERMISSION_CODE ){
            if(grantResults.isNotEmpty() &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadImageFromCamera.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
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

    private fun setupLoadImageFromCamera(){
        loadImageFromCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if(data!=null){
                    try{
                        this.user.Image = data.data.toString()
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



    fun setupInfoUser(objects: User) {
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
        setResult(Activity.RESULT_OK)
        finish()
    }
}