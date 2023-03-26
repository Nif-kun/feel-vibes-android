package com.example.feelvibes.home.search.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPostSearchPeopleBinding
import com.example.feelvibes.home.recycler.PostsRecyclerAdapter
import com.example.feelvibes.home.recycler.UsersRecyclerAdapter
import com.example.feelvibes.home.recycler.UsersRecyclerEvent
import com.example.feelvibes.model.PostModel
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBind


class SearchPeopleCategory : FragmentBind<FragmentPostSearchPeopleBinding>(FragmentPostSearchPeopleBinding::inflate), UsersRecyclerEvent {

    private lateinit var accountViewModel : AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        FVFireStoreHandler.queryUsersDisplayData(mainActivity.getSearchBar().text.toString()) { userIDs, exception ->
            if (userIDs.isNotEmpty()) {
                updateAdapter(userIDs)
                exception?.printStackTrace()
            } else {
                val noPostWarn = binding.root.findViewById<TextView>(R.id.noPostWarn)
                noPostWarn.visibility = View.VISIBLE
            }
        }
    }

    private fun updateAdapter(userIDs: ArrayList<String>) {
        val usersRecyclerView = binding.root.findViewById<RecyclerView>(R.id.usersRecyclerView)
        usersRecyclerView.adapter = UsersRecyclerAdapter(
            requireActivity(),
            this,
            userIDs
        )
    }

    override fun onUserClick(userId: String) {
        accountViewModel.selectedUserId = userId
        accountViewModel.currentNavStackId = R.id.action_global_postSearchFragment
        findNavController().navigate(R.id.action_global_profileFragment)
    }

}