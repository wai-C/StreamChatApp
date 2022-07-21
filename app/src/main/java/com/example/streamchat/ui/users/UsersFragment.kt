package com.example.streamchat.ui.users

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.streamchat.R
import com.example.streamchat.databinding.FragmentLoginBinding
import com.example.streamchat.databinding.FragmentUsersBinding
import com.example.streamchat.model.ChatUser
import com.example.streamchat.ui.MainActivity
import com.example.streamchatdemo.adapter.UsersAdapter
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User


class UsersFragment : Fragment() {

    private val usersAdapter by lazy { UsersAdapter()}
    private val client = ChatClient.instance()

    private var _userBinding: FragmentUsersBinding? = null
    private val userBinding get() = _userBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _userBinding = FragmentUsersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(userBinding.toolbar)
        return userBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupRecyclerView()
        queryAllUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_users, menu) //user our menu
        val search = menu.findItem(R.id.menuItem_search)
        val searchView = search.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isEmpty()){
                    queryAllUsers()
                }else{
                    searchUser(newText)
                }
                return true
            }

        })
        searchView?.setOnCloseListener { //when user clock on close btn, display all users
            queryAllUsers()
            false
        }
    }
    private fun setupToolBar(){
        userBinding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    private fun queryAllUsers() {
        val request = QueryUsersRequest(
            filter = Filters.ne("id", client.getCurrentUser()!!.id),
            offset = 0,
            limit = 100
        )
        //getAllUsers
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                usersAdapter.setUserList(users)
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }
    private fun searchUser(query: String) {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
            filter = filters,
            offset = 0,
            limit = 100
        )
        //get users according the query
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                usersAdapter.setUserList(users)
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }
    private fun setupRecyclerView(){
        userBinding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        userBinding.recyclerViewUsers.adapter = usersAdapter
    }
    override fun onDestroy() {
        super.onDestroy()
        _userBinding = null
    }
}