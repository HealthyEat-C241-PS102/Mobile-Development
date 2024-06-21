package com.capstone.healthyeat.ui.view.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.healthyeat.data.preferences.UserModel
import com.capstone.healthyeat.databinding.FragmentHomeBinding
import com.capstone.healthyeat.ui.view.detail.DetailActivity
import com.capstone.healthyeat.ui.view.login.LoginActivity
import com.capstone.healthyeat.utils.Injection
import com.capstone.healthyeat.utils.ViewModelFactory
import com.capstone.healthyeat.utils.createCustomTempFile
import com.capstone.healthyeat.utils.uriToFile

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeFragment : Fragment() {
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var user: UserModel
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val intentDetail = Intent(requireActivity(), DetailActivity::class.java)
            intentDetail.putExtra("currentPhotoPath", currentPhotoPath)
            intentDetail.putExtra("previousPage", "Home")
            startActivity(intentDetail)
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireActivity())
                val intentDetail = Intent(requireActivity(), DetailActivity::class.java)
                intentDetail.putExtra("currentPhotoPath", myFile.absolutePath)
                intentDetail.putExtra("previousPage", "Home")
                startActivity(intentDetail)
            }
        }
    }
    private fun allPermissionsGranted() = HomeFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity?.baseContext!!, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        Log.e("HomeFragment", user.toString())
    }

    private fun setupAction() {
        binding.frameLayout.setOnClickListener { }
        binding.frameLayout.setOnTouchListener { _, event -> onFrameLayoutTouch(event) }
        binding.logoutLayout.setOnClickListener {
            mainViewModel.logout()
        }
        binding.frameLayoutGallery.setOnClickListener {
            startGallery()
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(requireActivity().applicationContext, requireActivity().dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(viewLifecycleOwner) { user ->
            this.user = user
        }
    }

    private fun toLoginTest() {
        Log.e("MainActivity", "not login")
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onFrameLayoutTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("animationButton", "down")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.e("animationButton", "up")
                startTakePhoto()
            }
        }
        return false
    }

    private fun startTakePhoto() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                HomeFragment.REQUIRED_PERMISSIONS,
                HomeFragment.REQUEST_CODE_PERMISSIONS
            )
        }else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(requireActivity().packageManager)

            createCustomTempFile(requireActivity().application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.capstone.healthyeat",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == HomeFragment.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startTakePhoto()
            } else {
                Toast.makeText(requireActivity(), "Permissions Not Granted by The User.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}