package com.shaon2016.firebaserealtimechat.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val id: String?, val name: String, val email: String, val pass: String)