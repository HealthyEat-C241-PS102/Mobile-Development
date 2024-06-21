package com.capstone.healthyeat.data.preferences

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val email: String,
    val iat: String,
    val exp: String,
    val token: String,
    val isLogin: Boolean
): Parcelable