package com.example.trelloclone.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constant {
    const val COLLECTION_BOARD = "Board"
    const val COLLECTION_USERS = "USers"
    const val KEY_ID = "id"
    const val KEY_NAME = "name"
    const val KEY_EMAIL = "email"
    const val KEY_IMAGE = "image"
    const val KEY_MOBILE = "mobile"
    const val KEY_TOKEN = "fcmToken"

    const val KEY_NAME_BOARD = "name"
    const val KEY_IMAGE_BOARD = "image"
    const val KEY_CREATED_BY = "createdBy"
    const val KEY_ASSIGNED_TO = "assignedTo"
    const val KEY_DOCUMENT_ID = "documentId"
    const val KEY_TASK_LIST = "taskList"


    const val OBJECT_USER = "OBJECT_USER"
    const val OBJECT_BOARD = "OBJECT_BOARD"
    const val POSITION_CARD = "POSITION_CARD"
    const val POSITION_TASK = "POSITION_TASK"

    const val READ_PERMISSION_CODE = 1

    fun getExternalFile(uri: Uri,activity: Activity):String{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))!!
    }

    fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }
}