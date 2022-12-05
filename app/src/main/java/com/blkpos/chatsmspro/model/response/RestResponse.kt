package com.blkpos.chatsmspro.model.response

import java.util.ArrayList

open class RestResponse {
    var ok = false
    var errors: ArrayList<ErrorResponse>? = null

    fun getErrorDescription(): String{

        val errors = errors?.map {
            it.message
        }?.fold("", { acc, s ->

            acc + s + "\n"

        })

        return errors ?: ""

    }
}