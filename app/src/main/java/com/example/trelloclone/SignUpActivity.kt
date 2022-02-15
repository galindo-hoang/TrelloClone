package com.example.trelloclone

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.trelloclone.databinding.ActivitySignUpBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignUpActivity : BaseActivity() {
    private fun setToolbar() {
        setSupportActionBar(binding.tb)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()
        setToolbar()

        binding.btnSignUp.setOnClickListener {
            setupSignUp()
        }

    }

    private fun setupSignUp() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        if(checkValidForm(name,email,pass)){
            this.showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener { task ->
                this.hideProgressDialog()
                if(task.isSuccessful){
                    FirestoreClass().registerUser(this, User(task.result!!.user!!.uid,name,email))
                }else{
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkValidForm(name: String, email: String, pass: String): Boolean {
        return when{
            TextUtils.isEmpty(name) -> {
                this.showErrorSnackBar("Please fill Name")
                false
            }
            TextUtils.isEmpty(email) -> {
                this.showErrorSnackBar("Please fill Email")
                false
            }
            TextUtils.isEmpty(pass) -> {
                this.showErrorSnackBar("Please fill Password")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                this.showErrorSnackBar("Please fill Email form")
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegisterSuccess() {
        Toast.makeText(this,"You has successfully registered the email ",Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}