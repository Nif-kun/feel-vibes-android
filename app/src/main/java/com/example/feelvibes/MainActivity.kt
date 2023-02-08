package com.example.feelvibes

import android.app.AlertDialog
import android.content.*
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.feelvibes.databinding.ActivityMainBinding
import com.example.feelvibes.services.BackgroundSoundService
import com.example.feelvibes.utils.MusicPlayer
import com.example.feelvibes.utils.PermissionHandler
import com.example.feelvibes.view_model.LibraryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var backgroundSoundService: BackgroundSoundService
    private lateinit var libraryViewModel: LibraryViewModel
    //private var backgroundSoundServiceBounded: Boolean = false
    private var navController:  NavController? = null
    private var awake = false // onStart/onStop identifier
    var musicPlayer: MusicPlayer? = null

    companion object {
        const val HOME_FRAGMENT = 0
        const val LIBRARY_FRAGMENT = 1
        const val CREATE_FRAGMENT = 2
        const val SEARCH_FRAGMENT = 3
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundSoundService.BackgroundSoundBinder
            backgroundSoundService = binder.getService()
            if (musicPlayer == null)
                musicPlayer = backgroundSoundService.player
            // backgroundSoundServiceBounded = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            musicPlayer = null
            // backgroundSoundServiceBounded = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // force dark mode.
        binding = ActivityMainBinding.inflate(layoutInflater)
        libraryViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
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

    override fun onStart() {
        super.onStart()
        Intent(this, BackgroundSoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        awake = true
        setupStickyPlayer()
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        // backgroundSoundServiceBounded = false
        awake = false
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
        navController = navHostFragment?.findNavController()
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

    fun unpadMainView() {
        binding.mainNavHost.setPadding(0, 0, 0, 0)
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


    fun setupStickyPlayer() {
        if (awake && musicPlayer != null) {
            updateStickyPlayerLabel()
            updateStickyPlayerCoverArt()
            updateStickyPlayerProgressBar()
            musicPlayer!!.onCompletionListener {
                updateStickyPlayerLabel()
                updateStickyPlayerCoverArt()
                if (!musicPlayer!!.isPlaying())
                    binding.stickyPlayerInclude.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
            }
            onStickyPlayerEvent()
            onStickyPlayerPlayEvent()
            onStickyPlayerNextEvent()
            onStickyPlayerPreviousEvent()
        }
    }

    private fun updateStickyPlayerLabel() {
        binding.stickyPlayerInclude.titleLabel.text = musicPlayer!!.currentMusic?.title
    }

    private fun updateStickyPlayerCoverArt() {
        if (musicPlayer!!.currentMusic?.thumbnail != null)
            binding.stickyPlayerInclude.coverArtView.setImageBitmap(musicPlayer!!.currentMusic?.thumbnail)
        else if (musicPlayer!!.currentPlaylist?.thumbnail != null)
            binding.stickyPlayerInclude.coverArtView.setImageBitmap(musicPlayer!!.currentPlaylist?.thumbnail)
        else
            binding.stickyPlayerInclude.coverArtView.setImageResource(R.drawable.ic_album_24)
    }

    private fun updateStickyPlayerProgressBar() {
        val updateProgressBar = object : Runnable {
            override fun run() {
                if (awake) {
                    val max = if (musicPlayer!!.currentMusic?.duration?.toInt() != null) {
                        musicPlayer!!.currentMusic?.duration?.toInt()!!
                    } else { 0 }

                    if (binding.stickyPlayerInclude.progressBar.max != max) {
                        binding.stickyPlayerInclude.progressBar.max = max
                    }

                    binding.stickyPlayerInclude.progressBar.progress = musicPlayer!!.currentPosition()
                    binding.stickyPlayerInclude.progressBar.postDelayed(this, 50)
                }
            }
        }
        binding.stickyPlayerInclude.progressBar.post(updateProgressBar)
    }

    private fun onStickyPlayerEvent() {
        binding.stickyPlayerInclude.stickyPlayerLayout.setOnClickListener {

            navController?.navigate(R.id.action_global_playerFragment)
            libraryViewModel.navFromSticky = true
        }
    }

    private fun onStickyPlayerPlayEvent() {
        if (musicPlayer?.isPlaying() == true)
            binding.stickyPlayerInclude.playBtn.setImageResource(R.drawable.ic_pause_24)
        else
            binding.stickyPlayerInclude.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
        binding.stickyPlayerInclude.playBtn.setOnClickListener {
            if (musicPlayer?.isPlaying() == true) {
                binding.stickyPlayerInclude.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
                musicPlayer?.pause()
            } else {
                binding.stickyPlayerInclude.playBtn.setImageResource(R.drawable.ic_pause_24)
                musicPlayer?.play()
            }
        }
    }

    private fun onStickyPlayerNextEvent() {
        binding.stickyPlayerInclude.skipNextBtn.setOnClickListener {
            if (musicPlayer?.isPlaying()?.let { it1 -> musicPlayer?.next(it1) } == true) {
                updateStickyPlayerLabel()
                updateStickyPlayerCoverArt()
            } else
                Toast.makeText(this, "This is the last!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onStickyPlayerPreviousEvent() {
        binding.stickyPlayerInclude.skipPreviousBtn.setOnClickListener {
            if (musicPlayer?.isPlaying()?.let { it1 -> musicPlayer?.previous(it1) } == true) {
                updateStickyPlayerLabel()
                updateStickyPlayerCoverArt()
            } else
                Toast.makeText(this, "This is the first!", Toast.LENGTH_SHORT).show()
        }
    }

    fun showStickyPlayer() {
        binding.stickyPlayerInclude.stickyPlayerLayout.visibility = View.VISIBLE
    }
    fun hideStickyPlayer() {
        binding.stickyPlayerInclude.stickyPlayerLayout.visibility = View.GONE
    }

}