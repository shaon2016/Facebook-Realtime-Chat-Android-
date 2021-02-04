package com.shaon2016.firebaserealtimechat.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MyMessage(
    val id: String,
    val senderId: String,
    val senderName: String?,
    val message: String,
    val imageUrl: String?,
    val createdAt: String
)

