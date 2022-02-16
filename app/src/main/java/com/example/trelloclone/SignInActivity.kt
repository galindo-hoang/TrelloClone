package com.example.trelloclone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.trelloclone.databinding.ActivitySignInBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
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

    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        hideSystemBars()
        setToolbar()

        binding.btnSignIn.setOnClickListener {
            setupSignIn()
        }

    }


    private fun setupSignIn() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        if(checkValidForm(email,pass)){
            this.showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener { task ->
                this.hideProgressDialog()
                if(task.isSuccessful){
                    FirestoreClass().signInUser(this)
                }else{
                    Toast.makeText(this,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkValidForm(email: String, pass: String): Boolean {
        return when{
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

    fun userRegisterSuccess(toObject: User?) {
        Log.e("aaa",toObject.toString())
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra(Constant.OBJECT,toObject)
        startActivity(intent)
        finish()
    }
}