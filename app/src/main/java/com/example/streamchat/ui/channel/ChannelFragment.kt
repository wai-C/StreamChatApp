package com.example.streamchat.ui.channel

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.streamchat.R
import com.example.streamchat.databinding.FragmentChannelBinding
import com.example.streamchat.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.avatar.AvatarView
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory


class ChannelFragment : Fragment() {

    private lateinit var user: User
    private val args: ChannelFragmentArgs by navArgs()
    private val client = ChatClient.instance()
    private var _channelBinding: FragmentChannelBinding? = null
    private val channelBinding get() = _channelBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _channelBinding = FragmentChannelBinding.inflate(inflater,container,false)
        return channelBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUser()
        setupChannel()
        setupDrawer()
        //open the drawer from left side when click on it
        channelBinding.channelListHeaderView.setOnUserAvatarClickListener{
            channelBinding.drawerLayout.openDrawer(Gravity.LEFT)

        }
        channelBinding.channelsView.setChannelItemClickListener{channel->
            val action = ChannelFragmentDirections.actionChannelFragmentToChatFragment(channel.cid)
            findNavController().navigate(action)
        }
        channelBinding.channelListHeaderView.setOnActionButtonClickListener{
            findNavController().navigate(R.id.action_channelFragment_to_usersFragment)
        }
        channelBinding.channelsView.setChannelDeleteClickListener{
            deleteChannel(it)
        }
    }

    private fun setupUser() {
        if (client.getCurrentUser() == null) {
            //create user and token
                //hard code, can exchange to data from db
            user = if (args.chatUser.firstName.contains("hunter")) {
                User(
                    id = args.chatUser.username,
                    extraData = mutableMapOf(
                        "name" to args.chatUser.firstName,
                        "county" to "jp",
                        "image" to "https://cdn.dribbble.com/users/1231965/screenshots/4031718/media/b858f8f65cc6480018e59577fb0b7b73.jpg?compress=1&resize=400x300"
                    )
                )
            } else { //create
                User(
                    id = args.chatUser.username,
                    extraData = mutableMapOf( //key pairs value
                        "name" to args.chatUser.firstName
                    )
                )
            }
            //usually token from back-end, in this case just pass our user name as id
            val token = client.devToken(user.id)
            client.connectUser(
                user = user,
                token = token
            ).enqueue { result ->
                if (result.isSuccess) {
                    Log.d("ChannelFragment", "Success Connecting the User")
                } else {
                    Log.d("ChannelFragment", result.error().message.toString())
                }
            }
        }
    }
    //display all channel
    private fun setupChannel(){
        //using filters from stream chat sdk
        //filter the following filter objs such as Filters.eq(,) and Filters.in(,)
        val filters = Filters.and(Filters.eq("type","messaging"),Filters.`in`("members", listOf(client.getCurrentUser()!!.id)))
        //Stream chat UI.C :ChannelListHeaderView, ChannelListView need to use with the view model
        val viewModelFactory = ChannelListViewModelFactory(filters,ChannelListViewModel.DEFAULT_SORT)
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val listHeaderViewModel :ChannelListHeaderViewModel by viewModels()
        //bind their view
        listHeaderViewModel.bindView(channelBinding.channelListHeaderView,viewLifecycleOwner)
        listViewModel.bindView(channelBinding.channelsView,viewLifecycleOwner)
    }
    private fun deleteChannel(channel:Channel){
        ChatClient.instance().deleteChannel(channel.type, channel.cid).enqueue(){
            if(it.isSuccess){
                showSnake("Channel: ${channel.name} has been removed")
            }else{
                val currentFragmentName = (activity as MainActivity?)!!.getCurrentFragmentName()
                Log.e(currentFragmentName,it.error().message.toString())
            }
        }
    }
    private fun setupDrawer(){
        channelBinding.navigationView.setNavigationItemSelectedListener {  menuItem->
            if(menuItem.itemId == R.id.menuItem_logout){ logOut() }
            false
        }
        val currentUser = client.getCurrentUser()!! //handles NPE
        val headerView = channelBinding.navigationView.getHeaderView(0)
        val headerAvatar = headerView.findViewById<AvatarView>(R.id.avatarView)
        headerAvatar.setUserData(currentUser)
        val headerId = headerView.findViewById<TextView>(R.id.textView_id)
        headerId.text = currentUser.id //in our case is username
        val headerFN = headerView.findViewById<TextView>(R.id.textView_name)
        headerFN.text = currentUser.name

    }
    private fun logOut() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_ ->
            client.disconnect()
            findNavController().navigate(R.id.action_channelFragment_to_loginFragment)
            showSnake(getString(R.string.drawer_logout))
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle(getString(R.string.drawer_logout))
        builder.setMessage(getString(R.string.drawer_logout_msg))
        builder.create().show()
    }
    private fun showSnake(message: String) {
        Snackbar.make(requireView(),message,Snackbar.LENGTH_SHORT)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _channelBinding = null
    }

}