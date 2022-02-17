package com.example.trelloclone.firebase

import android.app.Activity
import android.util.Log
import com.example.trelloclone.activities.MainActivity
import com.example.trelloclone.activities.ProfileActivity
import com.example.trelloclone.activities.SignInActivity
import com.example.trelloclone.activities.SignUpActivity
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constant.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("---",getCurrentUserId())
                activity.userRegisterSuccess()
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun retrieveUser(activity: Activity){

        mFireStore.collection(Constant.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                val mapValue = document.data!!
//                val mobile = when(mapValue[Constant.KEY_MOBILE]){
//                    "" -> 0L
//                    else -> mapValue[Constant.KEY_MOBILE]
//                }
                val objects = User(
                    Id = mapValue[Constant.KEY_ID].toString(), Name = mapValue[Constant.KEY_NAME].toString(),
                    Email = mapValue[Constant.KEY_EMAIL].toString(), Image = mapValue[Constant.KEY_IMAGE].toString(),
                    Mobile = mapValue[Constant.KEY_MOBILE].toString().toLong(), fcmToken = mapValue[Constant.KEY_TOKEN].toString())
                when(activity){
                    is SignInActivity -> activity.userRegisterSuccess(objects)
                    is MainActivity -> activity.setupInfoUser(objects)
                    is ProfileActivity -> activity.setupInfoUser(objects)
                }
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun updateUser(activity: ProfileActivity, userHash: HashMap<String,Any>){
        mFireStore.collection(Constant.USERS)
            .document(getCurrentUserId())
            .update(userHash)
            .addOnSuccessListener {
                activity.setupUpdate()
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error update document")
            }
    }

    fun getCurrentUserId(): String{
        if(FirebaseAuth.getInstance().currentUser == null) return ""
        return FirebaseAuth.getInstance().currentUser!!.uid

    }
}