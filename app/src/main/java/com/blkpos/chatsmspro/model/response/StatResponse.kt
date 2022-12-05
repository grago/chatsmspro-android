package com.blkpos.chatsmspro.model.response

import android.os.Parcelable
import com.blkpos.chatsmspro.model.CurrentStat
import com.blkpos.chatsmspro.model.Stat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatResponse(
    val stats: ArrayList<Stat>,
    val current: CurrentStat
): Parcelable