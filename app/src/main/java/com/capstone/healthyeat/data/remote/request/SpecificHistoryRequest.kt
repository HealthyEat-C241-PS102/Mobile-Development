package com.capstone.healthyeat.data.remote.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpecificHistoryRequest(

    @field:SerializedName("informationName")
    val informationName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable