package com.blkpos.chatsmspro.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OauthResponse {
    @SerializedName("access_token")
    @Expose(serialize = true, deserialize = true)
    var accessToken: String? = null

    @SerializedName("expires_in")
    @Expose(serialize = true, deserialize = true)
    var expiresIn: Long = 0

    @SerializedName("token_type")
    @Expose(serialize = true, deserialize = true)
    var tokenType: String? = null
    @Expose(serialize = true, deserialize = true)
    var scope: String? = null

    @SerializedName("refresh_token")
    @Expose(serialize = true, deserialize = true)
    var refreshToken: String? = null
    @Expose(serialize = true, deserialize = true)
    var error: String? = null

}