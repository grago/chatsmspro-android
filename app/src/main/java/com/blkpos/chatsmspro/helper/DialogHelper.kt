package com.blkpos.chatsmspro.helper

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.blkpos.chatsmspro.R

object DialogHelper {

    fun showDialog(
        context: Context,
        title: Int,
        message: String,
        okButton: Int,
        listener: () -> Unit = {},
        cancelButton: Int = 0,
        cancelListener: () -> Unit = {},
    ){

        val builder = AlertDialog.Builder(context, R.style.Theme_CHATSMSPRO_Dialog)

        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(okButton, DialogInterface.OnClickListener { _, _ ->
                listener()
            })

        if ( cancelButton != 0 ) {
            builder.setNegativeButton(cancelButton, DialogInterface.OnClickListener { _, _ ->
                cancelListener()
            })

        }

        builder.show()


    }



}