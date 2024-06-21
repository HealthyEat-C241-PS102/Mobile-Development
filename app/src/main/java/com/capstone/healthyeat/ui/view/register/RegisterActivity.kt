package com.capstone.healthyeat.ui.view.register

import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.healthyeat.databinding.ActivityRegisterBinding
import com.capstone.healthyeat.ui.view.NetworkChangeReceiver
import com.capstone.healthyeat.utils.Injection
import com.capstone.healthyeat.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupNetworkReceiver()
        setupViewModel()
        setupFullScreen()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun setupNetworkReceiver() {
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    private fun setupAllertDialog() {
        alertDialog = AlertDialog.Builder(this).let {
            it.setTitle("Warning")
            it.setMessage("Please Connect Your Phone to The Internet")
            it.setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()
        }
    }

    private fun checkNetworkAndPopup() {
        if(!haveNetworkConnection()){
            if(!alertDialog.isShowing){
                alertDialog.show()
            }
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(application, dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) {
            showLoading(it.get(0))
        }

        registerViewModel.registerResponse.observe(this) {
            if (it != null) {
                if (it.message == "Register Successed") {
                    AlertDialog.Builder(this).apply {
                        setTitle("Great")
                        setMessage("Account Has Been Created")
                        setPositiveButton("Next") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Great")
                        setMessage(it.message)
                        setPositiveButton("Close", null)
                        create()
                        show()
                    }
                }
            }
        }
        registerViewModel.isLoading.observe(this) {
            showLoading(it.get(0))
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.registerButton.isClickable = !isLoading
        binding.registerButton.isVisible = !isLoading
        binding.progressBar.isVisible = isLoading
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            finish()
        }
        binding.registerButton.setOnClickListener {
            val name = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confPassword = binding.confirmPasswordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.usernameEditTextError.isVisible = true
                }
                email.isEmpty() -> {
                    binding.emailEditTextError.isVisible = true
                }
                password.isEmpty() -> {
                    binding.passwordEditTextError.isVisible = true
                }
                password.length < 6 -> {
                }
                else -> {
                    binding.usernameEditTextError.isVisible = false
                    binding.emailEditTextError.isVisible = false
                    binding.passwordEditTextError.isVisible = false
                    registerViewModel.register(name, email, password, confPassword)
                }
            }
        }
    }

    private fun setupFullScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            window.statusBarColor = Color.LTGRAY
        }
        supportActionBar?.hide()
    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI",
                    ignoreCase = true)
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE",
                    ignoreCase = true)
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }
}