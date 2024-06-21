package com.capstone.healthyeat.ui.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.healthyeat.data.HealthyEatRepository
import com.capstone.healthyeat.data.preferences.UserModel
import com.capstone.healthyeat.data.remote.response.SpecificHistoryResponse
import com.capstone.healthyeat.data.remote.response.UploadImageResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel(private val repo: HealthyEatRepository) : ViewModel() {
    private val _uploadImageResponse = MutableLiveData<UploadImageResponse>()
    val uploadImageResponse = _uploadImageResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _specificHistoryResponse = MutableLiveData<SpecificHistoryResponse>()
    val specificHistoryResponse: LiveData<SpecificHistoryResponse> = _specificHistoryResponse

    fun getUser(): LiveData<UserModel> {
        _isLoading.value = true
        return repo.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            Log.e("test", "test")
            repo.deleteUser()
        }
    }

    fun uploadImage(image: File) = runBlocking {
        _isLoading.value = true

        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )

        repo.uploadImage(imageMultipart).enqueue(object: Callback<UploadImageResponse> {
            override fun onResponse(
                call: Call<UploadImageResponse>,
                response: Response<UploadImageResponse>,
            ) {
                if(response.isSuccessful){
                    val uploadImageResponseRaw = response.body()!!
                    _uploadImageResponse.value = uploadImageResponseRaw
                    Log.e("MainViewModel", uploadImageResponseRaw.payload?.id!!)
                    Log.e("MainViewModel", uploadImageResponseRaw.payload?.informationName!!)
                    getSpecificHistory(uploadImageResponseRaw.payload?.id!!, uploadImageResponseRaw.payload.informationName!!)
                }else{
                    _isLoading.value = false
                    _uploadImageResponse.value = UploadImageResponse(500, null, "error")
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                _isLoading.value = false
                _uploadImageResponse.value = UploadImageResponse(500, null, "error")
            }
        })
    }

    fun getSpecificHistory(id: String, informationName: String) = runBlocking {
        val idInt = id.toInt()
        repo.getSpecificHistory(idInt, informationName).enqueue(object: Callback<SpecificHistoryResponse> {
            override fun onResponse(
                call: Call<SpecificHistoryResponse>,
                response: Response<SpecificHistoryResponse>,
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _specificHistoryResponse.value = response.body()!!
                }else{
                    _specificHistoryResponse.value = SpecificHistoryResponse(500, null, "request failed")
                    Log.e("DetailViewModel", "status code failed")
                }
            }

            override fun onFailure(call: Call<SpecificHistoryResponse>, t: Throwable) {
                _isLoading.value = false
                _specificHistoryResponse.value = SpecificHistoryResponse(500, null, "not found")
                Log.e("DetailViewModel", t.message!!)
            }
        })
    }
}