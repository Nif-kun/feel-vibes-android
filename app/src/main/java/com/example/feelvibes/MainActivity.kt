package com.example.feelvibes

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.feelvibes.databinding.ActivityMainBinding
import com.example.feelvibes.services.BackgroundSoundService
import com.example.feelvibes.utils.MusicPlayer
import com.example.feelvibes.utils.PermissionHandler
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var backgroundSoundService: BackgroundSoundService
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var accountViewModel : AccountViewModel
    private lateinit var homeViewModel: HomeViewModel

    private var awake = false // onStart/onStop identifier
    private var navController: NavController? = null

    companion object {
        const val HOME_FRAGMENT = 0
        const val LIBRARY_FRAGMENT = 1
        const val CREATE_FRAGMENT = 2
        const val SEARCH_FRAGMENT = 3
    }

    // Account related vars
    lateinit var mAuth: FirebaseAuth
    var profileClickedListener: (()->Unit)? = null

    // Music Player related vars
    //private var backgroundSoundServiceBounded: Boolean = false
    var musicPlayer: MusicPlayer? = null
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // force dark mode.
        libraryViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        mAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        // Binding process
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Extra on-load/setup
        createMainViewFunctions()
        musicPlayer?.notification?.build()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BackgroundSoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        awake = true
        setupStickyPlayer()
        if (musicPlayer?.notification?.visible() == true)
            musicPlayer?.notification?.dismiss()

        // Setup user
        checkAuth {
            accountViewModel.currentUser = it
            Log.d("Authentication", "logging in as ${mAuth.currentUser}")
        }
    }

   fun checkAuth(callback: (FirebaseUser?)-> Unit) {
        if (mAuth.currentUser != null) {
            mAuth.currentUser?.reload()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(mAuth.currentUser)
                }
            }?.addOnFailureListener { e ->
                e.printStackTrace()
                callback(null)
            }
        } else {
            callback(null)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        // backgroundSoundServiceBounded = false
        awake = false
        if (musicPlayer?.isPlaying() == true)
            musicPlayer?.notification?.show()
    }

    private fun requestPermission(permission : PermissionHandler.Permission){
        if (!permission.hasPermission) {
            AlertDialog.Builder(permission.activity)
                .setTitle("Requesting permission")
                .setMessage("FeelVibe requires the following permissions:\n" +
                        "• Storage access to collect music locally, and\n" +
                        "• Notifications to display currently playing music.\n\n" +
                        "Note: you may also grant the required permissions inside application or system Settings.")
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
        onFragmentChanged()
    }

    private fun setupMenuNavigation(navController : NavController?) {
        if (navController != null) {
            binding.mainFragNav.setupWithNavController(navController)
        }
    }

    private fun setupToolBar(navController : NavController?) {
        /*--Default ToolBar--*/
        // Back Button
        binding.customToolbar.toolBarBackBtn.setOnClickListener {
            navController?.popBackStack()
        }
        // Settings Button
        binding.customToolbar.toolBarSettingsBtn.setOnClickListener {
            navController?.navigate(R.id.settingsFragment)
        }
        // Search Button
        binding.customToolbar.toolBarSearchBtn.setOnClickListener {
            if (binding.searchBar.visibility == View.VISIBLE) {
                hideSearchBar()
            } else {
                showSearchBar()
            }
        }

        /*--Home ToolBar--*/
        // Profile Button
        binding.homeToolbar.profileBtn.setOnClickListener {
            profileClickedListener?.invoke()
        }
        // Search Button
        binding.homeToolbar.searchBtn.setOnClickListener {
            if (homeViewModel.layoutState == HomeViewModel.Layouts.HOME) {
                if (binding.searchBar.visibility == View.VISIBLE) {
                    hideSearchBar()
                } else {
                    showSearchBar()
                }
            }
        }
        // Logo Button
        binding.homeToolbar.logoBtn.setOnClickListener {

        }
    }

    fun padMainView() {
        binding.mainNavHost.setPadding(25, 0, 25, 0)
    }

    fun unpadMainView() {
        binding.mainNavHost.setPadding(0, 0, 0, 0)
    }

    fun isToolBarVisible(isHome: Boolean = false): Boolean {
        if (binding.toolBarLayout.visibility == View.VISIBLE) {
            if (isHome && binding.homeToolbar.toolBar.visibility == View.VISIBLE) {
                return true
            }
            if (binding.customToolbar.toolBar.visibility == View.VISIBLE) {
                return true
            }
        }
        return false
    }
    fun showToolBar(isHome: Boolean = false) {
        if (binding.toolBarLayout.visibility == View.GONE)
            binding.toolBarLayout.visibility = View.VISIBLE
        if (isHome) {
            if (isToolBarVisible())
                hideToolBar(isHome = false, includeLayout = false)
            binding.homeToolbar.toolBar.visibility = View.VISIBLE
        } else {
            if (isToolBarVisible(true))
                hideToolBar(isHome = true, includeLayout = false)
            binding.customToolbar.toolBar.visibility = View.VISIBLE
        }
    }
    fun hideToolBar(isHome: Boolean = false, includeLayout: Boolean = true, onlyLayout: Boolean = false) {
        if (includeLayout) {
            binding.toolBarLayout.visibility = View.GONE
            if (onlyLayout)
                return
        }
        if (isHome)
            binding.homeToolbar.toolBar.visibility = View.GONE
        else
            binding.customToolbar.toolBar.visibility = View.GONE
    }

    fun showSearchBar() {
        binding.searchBar.visibility = View.VISIBLE
    }
    fun hideSearchBar() {
        binding.searchBar.text.clear()
        binding.searchBar.visibility = View.GONE
    }
    fun getSearchBar(): EditText {
        return binding.searchBar
    }

    fun renameToolBar(title : String) {
        binding.customToolbar.toolBarTitle.text = title
    }

    fun showToolBarAppIcon() {
        binding.customToolbar.appIcon.visibility = View.VISIBLE
        binding.customToolbar.toolBarTitle.setPadding(0,0,0,0)
    }
    fun hideToolBarAppIcon() {
        binding.customToolbar.appIcon.visibility = View.GONE
        binding.customToolbar.toolBarTitle.setPadding(15,0,0,0)
    }

    fun showToolBarBack() {
        binding.customToolbar.toolBarBackBtn.visibility = View.VISIBLE
    }
    fun hideToolBarBack() {
        binding.customToolbar.toolBarBackBtn.visibility = View.GONE
    }

    fun showToolBarSearch() {
        binding.customToolbar.toolBarSearchBtn.visibility = View.VISIBLE
    }
    fun hideToolBarSearch() {
        binding.customToolbar.toolBarSearchBtn.visibility = View.GONE
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

    private fun onFragmentChanged() {
        navController?.addOnDestinationChangedListener { _, _, _ ->
            if (binding.searchBar.visibility == View.VISIBLE) {
                hideSearchBar()
            }
        }
    }
}