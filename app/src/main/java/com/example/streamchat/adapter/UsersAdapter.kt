package com.example.streamchatdemo.adapter

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.streamchat.databinding.UserRowLayoutBinding
import com.example.streamchat.ui.users.UsersFragmentDirections
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private val client = ChatClient.instance()
    private var userList = emptyList<User>()

    fun setUserList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            UserRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.binding.imageViewAvatar.setUserData(currentUser)
        holder.binding.textViewUsername.text = currentUser.id
        holder.binding.textViewLastActive.text = convertDate(currentUser.lastActive!!.time)
        holder.binding.rootLayout.setOnClickListener {
            createNewChannel(currentUser.id, holder)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    private fun convertDate(milliseconds: Long): String {
        return DateFormat.format("dd/MM/yyyy hh:mm a", milliseconds).toString()
    }

    private fun createNewChannel(selectedUser: String, holder: UsersViewHolder) {
        client.createChannel(
            channelType = "messaging",
            channelId = "",
            memberIds = listOf(client.getCurrentUser()!!.id, selectedUser), //this channel accessible only for user and selected user
            extraData = emptyMap()
        ).enqueue { result ->
            if (result.isSuccess) {
                val channel = result.data() //pass this channel id after created
                navigateToChatFragment(holder,channel.cid)
            } else {
                Log.e("UsersAdapter",result.error().message.toString())
            }
        }

    }

    private fun navigateToChatFragment(holder: UsersViewHolder, cid: String) {
        val action = UsersFragmentDirections.actionUsersFragmentToChatFragment(cid)
        holder.itemView.findNavController().navigate(action)
    }

    class UsersViewHolder(val binding: UserRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}











