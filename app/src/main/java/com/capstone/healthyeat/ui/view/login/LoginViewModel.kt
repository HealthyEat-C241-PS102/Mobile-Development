package com.capstone.healthyeat.ui.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.healthyeat.data.HealthyEatRepository
import com.capstone.healthyeat.data.preferences.UserModel
import com.capstone.healthyeat.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repo: HealthyEatRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return repo.getUser().asLiveData()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        repo.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                _isLoading.value = false
                if(response.isSuccessful){
                    val loginResponse = response.body()!!
                    _loginResponse.value = loginResponse
                    Log.e("LoginViewModel", "success")

                    viewModelScope.launch {
                        loginResponse.let { repo.saveUser(it) }
                    }
                }else if(response.code() == 401){
                    val loginResponse = LoginResponse("Invalid Email and Password")
                    _loginResponse.value = loginResponse

                }else{
                    val loginResponse = LoginResponse("Something Went Wrong")
                    _loginResponse.value = loginResponse
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val loginResponse = LoginResponse("Something Went Wrong")
                Log.e("test", "onFailure")
                _isLoading.value = false
                _loginResponse.value = loginResponse
            }
        })
    }
}