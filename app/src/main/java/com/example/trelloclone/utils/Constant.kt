package com.example.trelloclone.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constant {
    const val DOCUMENT_BOARD = "Board"
    const val DOCUMENT_USERS = "USers"
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

    const val READ_PERMISSION_CODE = 1

    fun getExternalFile(uri: Uri,activity: Activity):String{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))!!
    }
}