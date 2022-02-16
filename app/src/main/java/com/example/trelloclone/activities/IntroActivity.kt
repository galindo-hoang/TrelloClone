package com.example.trelloclone.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.*
import com.example.trelloclone.databinding.ActivityIntroBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.*

class IntroActivity : AppCompatActivity() {

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private lateinit var binding:ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        val typeFace = Typeface.createFromAsset(assets,"carbon bl.ttf")
        binding.tvSplashName.typeface = typeFace

        Executors.newSingleThreadScheduledExecutor().schedule({
            if(FirestoreClass().getCurrentUserId().isEmpty()){
                startActivity(Intent(this, LoginRegisterActivity::class.java))
            }else{
                startActivity(Intent(this,MainActivity::class.java))
            }
        },3,TimeUnit.SECONDS)


    }
}