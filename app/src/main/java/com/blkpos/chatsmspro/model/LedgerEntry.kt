package com.blkpos.chatsmspro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LedgerEntry(val stat: Stat?, val withdrawRequest: WithdrawRequest?): Parcelable