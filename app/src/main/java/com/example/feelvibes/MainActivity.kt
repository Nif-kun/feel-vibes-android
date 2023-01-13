package com.example.feelvibes

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.feelvibes.databinding.ActivityMainBinding
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistCollectionModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.PermissionHandler
import com.example.feelvibes.view_model.LibraryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val HOME_FRAGMENT = 0
        const val LIBRARY_FRAGMENT = 1
        const val CREATE_FRAGMENT = 2
        const val SEARCH_FRAGMENT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Check and request permission
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            requestPermission(PermissionHandler.ReadMediaAudio(this, true))
        } else {
            requestPermission(PermissionHandler.ReadExternalStorage(this, true))
        }
        createMainViewFunctions()
    }

    private fun requestPermission(permission : PermissionHandler.Permission){
        if (!permission.hasPermission) {
            AlertDialog.Builder(permission.activity)
                .setTitle("Requesting permission")
                .setMessage("FeelVibe requires storage access to collect music locally. " +
                        "You may also grant the required permissions inside application or system Settings.")
                .setCancelable(true)
                .setPositiveButton("Accept") { _: DialogInterface, _: Int ->
                    permission.check()
                }.setNegativeButton("Deny") { dialogInterface: DialogInterface, _ : Int ->
                    dialogInterface.cancel()
                }.show()
        }
    }

    private fun createMainViewFunctions() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host)
        val navController = navHostFragment?.findNavController()
        setupMenuNavigation(navController)
        setupToolBar(navController)
    }

    private fun setupMenuNavigation(navController : NavController?) {
        if (navController != null) {
            binding.mainFragNav.setupWithNavController(navController)
        }
    }

    private fun setupToolBar(navController : NavController?) {
        binding.customToolbar.toolBarBackBtn.setOnClickListener {
            navController?.popBackStack()
        }
        binding.customToolbar.toolBarSettingsBtn.setOnClickListener {
            navController?.navigate(R.id.settingsFragment)
        }
    }


    fun padMainView() {
        binding.mainNavHost.setPadding(25, 0, 25, 0)
    }

    fun showToolBar() {
        binding.customToolbar.toolBar.visibility = View.VISIBLE
    }
    fun hideToolBar() {
        binding.customToolbar.toolBar.visibility = View.GONE
    }

    fun renameToolBar(title : String) {
        binding.customToolbar.toolBarTitle.text = title
    }

    fun showToolBarBack() {
        binding.customToolbar.toolBarBackBtn.visibility = View.VISIBLE
    }
    fun hideToolBarBack() {
        binding.customToolbar.toolBarBackBtn.visibility = View.GONE
    }

    fun showToolBarSettings() {
        binding.customToolbar.toolBarSettingsBtn.visibility = View.VISIBLE
    }
    fun hideToolBarSettings() {
        binding.customToolbar.toolBarSettingsBtn.visibility = View.VISIBLE
    }

    fun showMainMenu() {
        binding.mainFragNav.visibility = View.VISIBLE
    }
    fun hideMainMenu() {
        binding.mainFragNav.visibility = View.GONE
    }

}