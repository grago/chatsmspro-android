package com.blkpos.chatsmspro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stat(
    var year: Int,
    var month: Int,
    var sentCount: Int,
    var amountEarned: Float
): Parcelable