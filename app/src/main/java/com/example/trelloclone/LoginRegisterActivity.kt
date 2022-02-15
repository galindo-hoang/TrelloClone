package com.example.trelloclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.trelloclone.databinding.ActivityLoginRegisterBinding
import com.example.trelloclone.firebase.FirestoreClass

class LoginRegisterActivity : BaseActivity() {

    private lateinit var binding:ActivityLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }
}