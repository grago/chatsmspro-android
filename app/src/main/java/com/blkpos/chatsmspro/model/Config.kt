package com.blkpos.chatsmspro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Config(val shareText: String, val minimumWithdrawAmount: Float): Parcelable