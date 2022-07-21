package com.example.streamchat.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.streamchat.R
import com.example.streamchat.databinding.FragmentChatBinding
import com.example.streamchat.databinding.FragmentLoginBinding
import com.example.streamchat.databinding.FragmentUsersBinding
import com.example.streamchat.ui.channel.ChannelFragmentArgs
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory


class ChatFragment : Fragment() {


    private val args: ChatFragmentArgs by navArgs()
    private var _chatBinding: FragmentChatBinding? = null
    private val chatBinding get() = _chatBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _chatBinding = FragmentChatBinding.inflate(inflater, container, false)
        setupMsg()
        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatBinding.messagesHeaderView.setBackButtonClickListener{
            requireActivity().onBackPressed() //back to channel frag
        }
    }

    private fun setupMsg(){
        val factory = MessageListViewModelFactory(cid = args.channelId)

        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListHeaderViewModel.bindView(chatBinding.messagesHeaderView, viewLifecycleOwner)
        messageListViewModel.bindView(chatBinding.messageList, viewLifecycleOwner)
        messageInputViewModel.bindView(chatBinding.messageInputView, viewLifecycleOwner)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _chatBinding = null
    }
}