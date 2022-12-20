package com.example.feelvibes

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.feelvibes.databinding.ActivityMainBinding
import com.example.feelvibes.utils.PermissionHandler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        setupMainMenu()
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


    private fun setupMainMenu() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host)
        if (navHostFragment != null) {
            Log.d("ThisHappened", "FRFR GODDAMNIT")
            val navController = navHostFragment.findNavController()
            binding.mainFragNav.setupWithNavController(navController)
        }
    }

    fun showToolBar() {
        binding.customToolbar.toolBar.visibility = View.VISIBLE
    }
    fun hideToolBar() {
        binding.customToolbar.toolBar.visibility = View.GONE
    }

    fun showMainMenu() {
        binding.mainFragNav.visibility = View.VISIBLE
    }
    fun hideMainMenu() {
        binding.mainFragNav.visibility = View.GONE
    }

}