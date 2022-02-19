package com.example.trelloclone.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constant {
    val BOARD = "Board"
    val USERS = "USers"
    val OBJECT = "OBJECT_USER"
    val KEY_ID = "id"
    val KEY_NAME = "name"
    val KEY_EMAIL = "email"
    val KEY_IMAGE = "image"
    val KEY_MOBILE = "mobile"
    val KEY_TOKEN = "fcmToken"

    val KEY_NAME_BOARD = "name"
    val KEY_IMAGE_BOARD = "image"
    val KEY_CREATEDBY = "createdBy"
    val KEY_ASSIGNEDTO = "assignedTo"
    var KEY_DOCUMENTID = "documentId"

    val OBJECT_USER = "OBJECT_USER"

    const val READ_PERMISSION_CODE = 1

    fun getExternalFile(uri: Uri,activity: Activity):String{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))!!
    }
}