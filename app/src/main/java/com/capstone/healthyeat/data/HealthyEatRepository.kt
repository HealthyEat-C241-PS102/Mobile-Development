package com.capstone.healthyeat.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.auth0.android.jwt.JWT
import com.capstone.healthyeat.data.preferences.UserModel
import com.capstone.healthyeat.data.preferences.UserPreference
import com.capstone.healthyeat.data.remote.request.LoginRequest
import com.capstone.healthyeat.data.remote.request.RegisterRequest
import com.capstone.healthyeat.data.remote.response.HistoryResponse
import com.capstone.healthyeat.data.remote.response.LoginResponse
import com.capstone.healthyeat.data.remote.response.RegisterResponse
import com.capstone.healthyeat.data.remote.response.SpecificHistoryResponse
import com.capstone.healthyeat.data.remote.response.UploadImageResponse
import com.capstone.healthyeat.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.Call

class HealthyEatRepository private constructor(

    private val apiService: ApiService,
    private val userPreference: UserPreference,
){
    fun getUser(): Flow<UserModel> {
        return userPreference.getUser();
    }

    suspend fun getAllDummyHistory(): Call<HistoryResponse> {
        return apiService.getAllDummyHistory("Bearer " + getUser().first().token)
    }

    suspend fun getSpecificHistory(id: Int, informationName: String): Call<SpecificHistoryResponse>{
        return apiService.getSpecificHistory("Bearer " + getUser().first().token,id,informationName)
    }

    suspend fun uploadImage(file: MultipartBody.Part): Call<UploadImageResponse>{
        return apiService.uploadImage("Bearer " + getUser().first().token, file)
    }

    fun register(name: String, email: String, password: String, confPassword: String): Call<RegisterResponse> {
        val resgisterRequest = RegisterRequest(name, email, password, confPassword)
        return apiService.register(resgisterRequest)
    }

    suspend fun saveUser(loginResponse: LoginResponse) {
        val token = loginResponse.token
        val jwt = JWT(token!!)

        val iat: String = jwt.getClaim("iat").asString()!!
        val exp: String = jwt.getClaim("exp").asString()!!
        val email: String = jwt.getClaim("email").asString()!!

        val user = UserModel(email, iat, exp, token, true)
        userPreference.saveUser(user)
    }

    suspend fun deleteUser() {
        userPreference.deleteUser()
    }

    fun login(name: String, password: String): Call<LoginResponse> {
        val loginRequest = LoginRequest(name, password)
        return apiService.login(loginRequest)
    }

    companion object {
        @Volatile
        private var instance: HealthyEatRepository? = null
        fun getInstance(
            apiService: ApiService,
            dataStore: DataStore<Preferences>,
        ): HealthyEatRepository =
            instance ?: synchronized(this) {
                instance ?: HealthyEatRepository(apiService, UserPreference.getInstance(dataStore))
            }.also { instance = it }
    }
}