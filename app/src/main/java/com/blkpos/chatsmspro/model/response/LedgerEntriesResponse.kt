package com.blkpos.chatsmspro.model.response

import android.os.Parcelable
import com.blkpos.chatsmspro.model.CurrentStat
import com.blkpos.chatsmspro.model.LedgerEntry
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LedgerEntriesResponse(
    val ledgerEntries: ArrayList<LedgerEntry>,
    val current: CurrentStat
): Parcelable