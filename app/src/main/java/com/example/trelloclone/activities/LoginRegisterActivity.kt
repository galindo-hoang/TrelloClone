package com.example.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import com.example.trelloclone.databinding.ActivityLoginRegisterBinding

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