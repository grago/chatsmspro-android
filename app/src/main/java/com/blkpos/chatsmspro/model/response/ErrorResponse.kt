package com.blkpos.chatsmspro.model.response

import com.google.gson.annotations.Expose

class ErrorResponse {
    @Expose(serialize = true, deserialize = true)
    var message: String? = null
    @Expose(serialize = true, deserialize = true)
    var propertyPath: String? = null

}
