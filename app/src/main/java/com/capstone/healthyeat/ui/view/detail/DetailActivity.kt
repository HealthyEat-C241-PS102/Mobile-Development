package com.capstone.healthyeat.ui.view.detail

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.healthyeat.R
import com.capstone.healthyeat.data.remote.response.SpecificHistoryResponse
import com.capstone.healthyeat.databinding.ActivityDetailBinding
import com.capstone.healthyeat.ui.view.main.MainViewModel
import com.capstone.healthyeat.utils.Injection
import com.capstone.healthyeat.utils.ViewModelFactory
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var id: String
    private lateinit var informationName: String
    private lateinit var previousPage: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupFullScreen()
        previousPage = intent.getStringExtra("previousPage")!!
        if(previousPage.equals("History")){
            getExtraHistory()
            setupViewModelHistory()
        }else if(previousPage.equals("Home")){
            getFileFromExtra()
            setupViewModelHome()
        }
        setupNotFoundAction()
    }

    private fun setupNotFoundAction() {
        binding.goBackHomeButton.setOnClickListener {
            finish()
        }
    }

    private fun getFileFromExtra() {
        val currentPhotoPath = intent.getStringExtra("currentPhotoPath")
        val myFile = File(currentPhotoPath)
        getFile = myFile
    }

    private fun setupViewModelHome() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(applicationContext, dataStore))
        )[MainViewModel::class.java]

        mainViewModel.uploadImage(getFile!!)

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.specificHistoryResponse.observe(this) {
            setupData(it!!)
        }

        mainViewModel.uploadImageResponse.observe(this) {
            if (it.statusCode != 200) {
                showNotFound()
            }
        }
    }

    private fun getExtraHistory() {
        id = intent.getStringExtra("id")!!
        informationName = intent.getStringExtra("informationName")!!
    }

    private fun setupViewModelHistory() {
        detailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(applicationContext, dataStore))
        )[DetailViewModel::class.java]

        detailViewModel.getSpecificHistory(id, informationName)

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.specificHistoryResponse.observe(this) {
            setupData(it!!)
        }
    }

    private fun setupData(it: SpecificHistoryResponse) {
        if(it.statusCode == 200) {
            val condition = it.payload?.queryHistory?.get(0)?.condition

            Glide.with(applicationContext)
                .load(it.payload?.queryHistory?.get(0)?.urlImage)
                .into(binding.pictureContent)

            val sourceString  = "<b>" + it.payload?.queryInformation?.get(0)?.botanicalName + "</b> " + ", " + it.payload?.queryInformation?.get(0)?.description;
            binding.descriptionContentText.setText(Html.fromHtml(sourceString))
            binding.interestContentText.text = it.payload?.queryInformation?.get(0)?.benefit!!

            binding.nameContentText.text = it.payload?.queryHistory?.get(0)?.informationName
            if(condition.equals("Good")){
                val alphaPercentage =
                    it.payload?.queryHistory?.get(0)?.percentage!!.split(".")[0].toInt();
                val color = ContextCompat.getColor(applicationContext, R.color.white)
                val alphaColor = ColorUtils.setAlphaComponent(color, (alphaPercentage * 255) / 100)

                binding.circlePercentage.setCardBackgroundColor(alphaColor)
                binding.conditionContentText.text = condition
                binding.conditionContentText.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }else{
                val alphaPercentage =
                    it.payload?.queryHistory?.get(0)?.percentage!!.split(".")[0].toInt();
                val color = ContextCompat.getColor(applicationContext, R.color.black)
                val alphaColor = ColorUtils.setAlphaComponent(color, (alphaPercentage * 255) / 100)

                binding.circlePercentage.setCardBackgroundColor(alphaColor)
                binding.conditionContentText.text = condition
                binding.conditionContentText.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            }
            binding.allergyContentText.text = it.payload?.queryInformation?.get(0)?.allergy
            binding.energyContentText.text = it.payload?.queryInformation?.get(0)?.energy
            binding.waterContentText.text = it.payload?.queryInformation?.get(0)?.water
            binding.proteinContentText.text = it.payload?.queryInformation?.get(0)?.protein
            binding.totalfatContentText.text = it.payload?.queryInformation?.get(0)?.totalFat
            binding.carbohydaratesContentText.text = it.payload?.queryInformation?.get(0)?.carbohydrates
            binding.fiberContentText.text = it.payload?.queryInformation?.get(0)?.fiber
            binding.sugarContentText.text = it.payload?.queryInformation?.get(0)?.sugars
            binding.calsiumContentText.text = it.payload?.queryInformation?.get(0)?.calsium
            binding.ironContentText.text = it.payload?.queryInformation?.get(0)?.iron
        }else{
            showNotFound()
        }
    }

    private fun setupFullScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            window.statusBarColor = Color.LTGRAY
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean){
        binding.detailPage.isVisible = !isLoading
        binding.loadingPage.isVisible = isLoading
    }

    private fun showNotFound(){
        binding.detailPage.isVisible = false
        binding.notFoundPage.isVisible = true
    }
}