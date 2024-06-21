package com.capstone.healthyeat.ui.view.main

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.capstone.healthyeat.R
import com.capstone.healthyeat.data.preferences.UserModel
import com.capstone.healthyeat.databinding.ActivityMainBinding
import com.capstone.healthyeat.databinding.TabTitleBinding
import com.capstone.healthyeat.ui.view.NetworkChangeReceiver
import com.capstone.healthyeat.ui.view.SectionsPagerAdapter
import com.capstone.healthyeat.ui.view.login.LoginActivity
import com.capstone.healthyeat.utils.Injection
import com.capstone.healthyeat.utils.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var user: UserModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupNetworkReceiver()
        setupViewModel()
        setupFullScreen()
        setupViewPager()
    }

    private fun setupNetworkReceiver() {
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    private fun setupAllertDialog() {
        alertDialog = AlertDialog.Builder(this).let {
            it.setTitle("Warning")
            it.setMessage("Please cCnnect Your Phone to The Internet")
            it.setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun checkNetworkAndPopup() {
        if(!haveNetworkConnection()){
            if(!alertDialog.isShowing){
                alertDialog.show()
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(applicationContext, dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                Log.e("MainActivity", "login")
                this.user = user
            } else {
                Log.e("MainActivity", "not login")
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this@MainActivity)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 1
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->

        }.attach()

        val bindingMenuItem = TabTitleBinding.inflate(LayoutInflater.from(applicationContext))
        bindingMenuItem.imageViewMenuIcon.setImageResource(R.drawable.history)
        val frameLayout1 = bindingMenuItem.root
        tabs.getTabAt(0)?.customView = frameLayout1

        val bindingMenuItem2 = TabTitleBinding.inflate(LayoutInflater.from(applicationContext))
        bindingMenuItem2.imageViewMenuIcon.setImageResource(R.drawable.home)
        val frameLayout2 = bindingMenuItem2.root
        tabs.getTabAt(1)?.customView = frameLayout2
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