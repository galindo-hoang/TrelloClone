package com.example.trelloclone.firebase

import android.app.Activity
import android.util.Log
import com.example.trelloclone.activities.*
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun createBoard(activity: CreateBoardActivity,boardInfo: Board){
        mFireStore.collection(Constant.BOARD)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.finishCreateBoard()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

//    fun fetchBoardByID()

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

    fun fetchBoardByID(activity: MainActivity) {
        mFireStore.collection(Constant.BOARD)
            .whereArrayContains(Constant.KEY_ASSIGNEDTO,this.getCurrentUserId())
            .get()
            .addOnSuccessListener { it ->
                val list = ArrayList<Board>()
                for (i in it.documents){
                    val dataHash = i.data
                    val assign = arrayListOf<String>()
                    for(mem in (dataHash!![Constant.KEY_ASSIGNEDTO] as ArrayList<*>)) assign.add(mem.toString())
                    val data = Board(
                        Name = dataHash[Constant.KEY_NAME_BOARD].toString(),
                        Image = dataHash[Constant.KEY_IMAGE_BOARD].toString(),
                        CreatedBy = dataHash[Constant.KEY_CREATEDBY].toString(),
                        DocumentId = i.id, AssignedTo = assign)
                    list.add(data)
                }
                activity.onSuccessFetchBoard(list)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
}