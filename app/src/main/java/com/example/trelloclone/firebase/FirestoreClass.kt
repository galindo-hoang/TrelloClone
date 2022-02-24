package com.example.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
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
        mFireStore.collection(Constant.COLLECTION_BOARD)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.finishCreateBoard()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constant.COLLECTION_USERS)
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
        mFireStore.collection(Constant.COLLECTION_USERS)
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
        mFireStore.collection(Constant.COLLECTION_USERS)
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
        mFireStore.collection(Constant.COLLECTION_BOARD)
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

    fun updateListInBoard(activity: Activity, listHash: HashMap<String,Any>, boardID: String){
        mFireStore.collection(Constant.COLLECTION_BOARD)
            .document(boardID)
            .update(listHash)
            .addOnSuccessListener {
                when(activity){
                    is TaskListActivity -> activity.successUpdateListInBoard()
                    is CardDetailActivity -> activity.successUpdateNameOfCardInList()
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is TaskListActivity -> activity.hideProgressDialog()
                    is CardDetailActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while add list in board.", e)
            }
    }

    fun fetchListInBoard(activity: TaskListActivity, board: Board) {
        mFireStore.collection(Constant.COLLECTION_BOARD)
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

        mFireStore.collection(Constant.COLLECTION_BOARD)
            .document(documentId)
            .update(listHash)
            .addOnSuccessListener {
                activity.successEditListInBoard()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while edit title of list in board.", e)
            }
    }

    fun fetchUserByBoard(activity: MembersActivity, arrayMembers: ArrayList<String>) {
        mFireStore.collection(Constant.COLLECTION_USERS)
            .whereIn(Constant.KEY_ID,arrayMembers)
            .get()
            .addOnSuccessListener {
                val usersList = ArrayList<User>()
                for (i in it.documents) {
                    // Convert all the document snapshot to the object using the data model class.
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                activity.successFetchUserByBoard(usersList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetch users in board.", e)
            }
    }

    fun fetchUserByEmail(activity: MembersActivity, email: String) {
        mFireStore.collection(Constant.COLLECTION_USERS)
            .whereEqualTo(Constant.KEY_EMAIL,email)
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    activity.successFetchUserByEmail(it.documents[0].toObject(User::class.java))
                }else{
                    activity.hideProgressDialog()
                    Toast.makeText(activity,"Email is not valid",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetch users by email.", e)
            }
    }

    fun updateMembersInBoard(activity: MembersActivity, userHash: HashMap<String, Any>, documentId: String) {
        mFireStore.collection(Constant.COLLECTION_BOARD)
            .document(documentId)
            .update(userHash)
            .addOnSuccessListener {
                activity.successUpdateMembersInBoard()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while update members in board.", e)
            }
    }

    fun fetchUserListInBoard(activity: MembersActivity, documentId: String) {
        mFireStore.collection(Constant.COLLECTION_BOARD)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                val board = it.toObject(Board::class.java)
                activity.successFetchUserListInBoard(board!!.AssignedTo)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while update members in board.", e)
            }
    }
}