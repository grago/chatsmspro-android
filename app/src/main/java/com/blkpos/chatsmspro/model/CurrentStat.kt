package com.blkpos.chatsmspro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentStat(
    var sentCount: Int,
    var amountToEarn: Float
): Parcelable