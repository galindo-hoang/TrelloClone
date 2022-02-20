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
        Log.e("---createBoard",boardInfo.toString())
        mFireStore.collection(Constant.DOCUMENT_BOARD)
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
        mFireStore.collection(Constant.DOCUMENT_USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterSuccess()
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun retrieveUser(activity: Activity){
        mFireStore.collection(Constant.DOCUMENT_USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                val user = document.toObject(User::class.java)
                when(activity){
                    is SignInActivity -> activity.successLoginUser(user)
                    is MainActivity -> activity.successRetrieveUser(user!!)
                    is ProfileActivity -> activity.successRetrieveUser(user!!)
                }
            }
            .addOnCanceledListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun updateUser(activity: ProfileActivity, userHash: HashMap<String,Any>){
        mFireStore.collection(Constant.DOCUMENT_USERS)
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
        mFireStore.collection(Constant.DOCUMENT_BOARD)
            .whereArrayContains(Constant.KEY_ASSIGNED_TO,this.getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val list = ArrayList<Board>()
                for (i in it.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.DocumentId = i.id
                    list.add(board)
                }
                activity.onSuccessFetchBoard(list)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetch boards.", e)
            }
    }

    fun addListIntoBoard(activity: TaskListActivity, listHash: HashMap<String,Any>,boardID: String){
        mFireStore.collection(Constant.DOCUMENT_BOARD)
            .document(boardID)
            .update(listHash)
            .addOnSuccessListener {
                activity.successAddListIntoBoard()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while add list in board.", e)
            }
    }

    fun fetchListInBoard(activity: TaskListActivity, board: Board) {
        mFireStore.collection(Constant.DOCUMENT_BOARD)
            .document(board.DocumentId)
            .get()
            .addOnSuccessListener {
                Log.e("---d",it.toObject(Board::class.java)!!.toString())
                activity.successFetchListInBoard(it.toObject(Board::class.java)!!.taskList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetch lists in board.", e)
            }
    }

    fun editTitleListInBoard(activity: TaskListActivity, listHash: HashMap<String, Any>, documentId: String) {

        mFireStore.collection(Constant.DOCUMENT_BOARD)
            .document(documentId)
            .update(listHash)
            .addOnSuccessListener {
                activity.successEditListInBoard()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while add list in board.", e)
            }
    }
}