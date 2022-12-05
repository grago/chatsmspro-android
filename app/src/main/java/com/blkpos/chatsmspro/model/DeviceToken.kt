package com.blkpos.chatsmspro.model

data class DeviceToken(
    var deviceType: String,
    var deviceId: String,
    var token: String,
    var appVersion: String,
)
