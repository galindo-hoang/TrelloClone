package com.example.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.adapter.BoardAdapter
import com.example.trelloclone.databinding.ActivityMainBinding
import com.example.trelloclone.databinding.NavHeaderMainBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var loadUserUpdate: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var createBoard: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var binding:ActivityMainBinding
    private lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        binding.navView.setNavigationItemSelectedListener (this)
        setupLoadUserUpdate()
        setupCreateBoard()
        FirestoreClass().retrieveUser(this)
        FirestoreClass().fetchBoardByID(this)

        binding.icMainActivity.fabCreateBoard.setOnClickListener {
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constant.OBJECT_USER, user)
            createBoard.launch(intent)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(findViewById(R.id.tbAppBar))
        findViewById<Toolbar>(R.id.tbAppBar).setNavigationIcon(R.drawable.ic_baseline_menu_24)
        findViewById<Toolbar>(R.id.tbAppBar).setNavigationOnClickListener {
            binding.dl.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding.dl.isDrawerOpen(GravityCompat.START)){
            binding.dl.closeDrawer(GravityCompat.START)
        }else{
            this.doubleBackToExit()
        }
    }


    fun setupLoadUserUpdate(){
        loadUserUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                FirestoreClass().retrieveUser(this@MainActivity)
            }
        }
    }

    fun setupCreateBoard(){
        createBoard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                FirestoreClass().fetchBoardByID(this@MainActivity)
            }
        }
    }


    fun setupInfoUser(user: User) {
        this.user = user
        val navBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        navBinding.tvNameNavigation.text = user.Name
        navBinding.tvEmailNavigation.text = user.Email
        Glide
            .with(this)
            .load(user.Image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navBinding.profileImage)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itSignOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.itProfile -> {
                loadUserUpdate.launch(Intent(this,ProfileActivity::class.java))
                item.isCheckable = false
            }
        }
        binding.dl.closeDrawer(GravityCompat.START)
        return true
    }

    fun onSuccessFetchBoard(list: ArrayList<Board>) {
        if(list.isNotEmpty()){
            binding.icMainActivity.icAppBarMain.rcvBoardList.layoutManager = LinearLayoutManager(this)
            binding.icMainActivity.icAppBarMain.rcvBoardList.adapter = BoardAdapter(list)
            binding.icMainActivity.icAppBarMain.rcvBoardList.visibility = View.VISIBLE
            binding.icMainActivity.icAppBarMain.tvEmpty.visibility = View.INVISIBLE
        }else{
            binding.icMainActivity.icAppBarMain.rcvBoardList.visibility = View.INVISIBLE
            binding.icMainActivity.icAppBarMain.tvEmpty.visibility = View.VISIBLE
        }
    }
}