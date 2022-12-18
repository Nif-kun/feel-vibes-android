package com.example.feelvibes

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonWriter
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.utils.MusicDataHandler
import com.example.feelvibes.utils.PermissionHandler
import java.io.File
import java.io.Reader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request permission
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            requestPermission(PermissionHandler.ReadMediaAudio(this, true))
        } else {
            requestPermission(PermissionHandler.ReadExternalStorage(this, true))
        }
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