package com.example.feelvibes

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.utils.MusicDataHandler
import com.example.feelvibes.utils.PermissionHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        // Check and request permission
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            requestPermission(PermissionHandler.ReadMediaAudio(this, true))
        } else {
            requestPermission(PermissionHandler.ReadExternalStorage(this, true))
        }

        // TODO Ref:0
        // This won't trigger on first-time installs due to lack of permissions.
        viewModel.updateMusicDataList(MusicDataHandler.Collect(this).data)
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
}