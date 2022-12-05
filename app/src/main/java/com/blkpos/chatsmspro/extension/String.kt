package com.blkpos.chatsmspro.extension

import android.content.Context

fun String.localized(context: Context): String? {
    try {

        val resId = context.resources.getIdentifier(this, "string", context.packageName)

        return context.getString(resId)
    }catch (ex : Exception){

    }
    return null

}

fun String.localizedWithFormat(context: Context, vararg formatArgs: Any?): String? {
    try {

        val resId = context.resources.getIdentifier(this, "string", context.packageName)

        return context.getString(resId, *formatArgs)
    }catch (ex : Exception){

    }
    return null

}

