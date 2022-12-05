package com.blkpos.chatsmspro.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @Expose(serialize = true, deserialize = true)
    var id: Int? = null,
    @Expose(serialize = true, deserialize = true)
    var email: String? = null,
    @Expose(serialize = true, deserialize = true)
    var plainPassword: String? = null,
    @Expose(serialize = true, deserialize = true)
    var firstName: String? = null,
    @Expose(serialize = true, deserialize = true)
    var lastName: String? = null,
    @Expose(serialize = true, deserialize = true)
    var mobileNumber: String? = null,
    @Expose(serialize = true, deserialize = true)
    var mobilePlan: Plan? = null,
    @Expose(serialize = true, deserialize = true)
    var country: Country? = null,
    @Expose(serialize = true, deserialize = true)
    var carrier: Carrier? = null,
    @Expose(serialize = true, deserialize = true)
    var smsAmount: Int? = null,
    @Expose(serialize = true, deserialize = true)
    var dailySmsAmount: Int? = null,
    @Expose(serialize = false, deserialize = true)
    var lastWithdrawEmail: String? = null
): Parcelable {

    companion object {
        var currentUser: User? = null
    }


}