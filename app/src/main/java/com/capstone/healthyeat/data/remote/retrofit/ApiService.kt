package com.capstone.healthyeat.data.remote.retrofit

import com.capstone.healthyeat.data.remote.request.LoginRequest
import com.capstone.healthyeat.data.remote.request.RegisterRequest
import com.capstone.healthyeat.data.remote.response.HistoryResponse
import com.capstone.healthyeat.data.remote.response.LoginResponse
import com.capstone.healthyeat.data.remote.response.RegisterResponse
import com.capstone.healthyeat.data.remote.response.SpecificHistoryResponse
import com.capstone.healthyeat.data.remote.response.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("/gethistory")
    fun getAllDummyHistory(@Header("Authorization") token: String): Call<HistoryResponse>

    @GET("/getspecifichistory")
    fun getSpecificHistory(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("InformationName") informationName: String
    ): Call<SpecificHistoryResponse>

    @Multipart
    @POST("/upload/image")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<UploadImageResponse>
}