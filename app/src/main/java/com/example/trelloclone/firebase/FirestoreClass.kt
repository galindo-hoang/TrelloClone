package com.example.trelloclone.firebase

import android.util.Log
import com.example.trelloclone.SignInActivity
import com.example.trelloclone.SignUpActivity
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

    fun SignInUser(activity: SignInActivity){
        mFireStore.collection(Constant.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                activity.userRegisterSuccess(it.toObject(User::class.java))
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun getCurrentUserId(): String{
        if(FirebaseAuth.getInstance().currentUser == null) return ""
        return FirebaseAuth.getInstance().currentUser!!.uid

    }
}