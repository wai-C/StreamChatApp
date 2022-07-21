package com.example.streamchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.streamchat.R
import com.example.streamchat.model.ChatUser
import com.example.streamchat.ui.login.LoginFragment
import com.example.streamchat.ui.login.LoginFragmentDirections
import io.getstream.chat.android.client.ChatClient

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val client = ChatClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.navHostFragment)
        //?: is for type safe if there has null value
        if(navController.currentDestination?.label.toString().contains("login")){
            val currentUser = client.getCurrentUser()
            if(currentUser!=null){
                //pass value to channel fragment using safe args
                val user = ChatUser(currentUser.name,currentUser.id)
                val action = LoginFragmentDirections.actionLoginFragmentToChannelFragment(user)
                navController.navigate(action)
            }
        }
    }
    fun getCurrentFragmentName():String {
        return navController.currentDestination?.displayName ?: ""
    }
}