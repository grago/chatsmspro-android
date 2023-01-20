package com.blkpos.chatsmspro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class WithdrawRequest(val fulfilledAt: Date?, val amount: Float): Parcelable