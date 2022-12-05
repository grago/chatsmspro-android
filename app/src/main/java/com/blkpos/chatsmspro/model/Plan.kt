package com.blkpos.chatsmspro.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Plan(
    @Expose(serialize = true, deserialize = true)
    val id: Int,
    @Expose(serialize = false, deserialize = true)
    var name: String

): Parcelable