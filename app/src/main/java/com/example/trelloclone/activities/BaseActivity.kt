package com.example.trelloclone.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivityBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {
    private var doubleClickToExit = false
    private lateinit var mProgessDialog: Dialog
    private lateinit var binding:ActivityBaseBinding

    fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun showProgressDialog(text: String){
        this.mProgessDialog = Dialog(this)
        mProgessDialog.setContentView(R.layout.dialog_progress)
        mProgessDialog.findViewById<TextView>(R.id.tvProgress).text = text
        mProgessDialog.show()
    }

    fun hideProgressDialog(){
        mProgessDialog.dismiss()
    }

    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleClickToExit){
            onBackPressed()
        }else{
            doubleClickToExit = true
            Toast.makeText(this, R.string.please_click_back_again_to_exit,Toast.LENGTH_SHORT).show()
            Executors.newSingleThreadScheduledExecutor().schedule({
                doubleClickToExit = false
            },2,TimeUnit.SECONDS)
        }
    }

    fun showErrorSnackBar(meesage: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),meesage,Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBar_error_color))
        snackBar.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}