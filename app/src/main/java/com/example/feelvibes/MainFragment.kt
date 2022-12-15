package com.example.feelvibes

import androidx.fragment.app.Fragment
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.databinding.FragmentMainBinding
import com.example.feelvibes.home.HomeFragment
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.settings.SettingsFragment

class MainFragment : FragmentBind<FragmentMainBinding>(FragmentMainBinding::inflate) {
    override fun onReady() {
        showActionbar()
        loadFragment(HomeFragment()) // Starting fragment
        buildMainMenu()
    }

    private fun buildMainMenu(){
        binding.mainFragNav.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.home_fragment -> HomeFragment()
                R.id.library_fragment -> LibraryFragment()
                R.id.create_fragment -> CreateFragment()
                R.id.settings_fragment -> SettingsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_frag_frame, fragment)
            .commit()
    }

}