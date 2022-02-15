package com.example.trelloclone

import android.os.Bundle
import com.example.trelloclone.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
//    private fun setToolbar() {
//        setSupportActionBar(binding.tb)
//        if(supportActionBar != null){
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
//        }
//        binding.tb.setNavigationOnClickListener {
//            doubleBackToExit()
//        }
//    }

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setToolbar()

    }
}