package com.example.streamchat.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.streamchat.R
import com.example.streamchat.databinding.FragmentLoginBinding
import com.example.streamchat.model.ChatUser
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private var _loginBinding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val loginBinding get() = _loginBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _loginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding.button.setOnClickListener {
            authUser()
        }
    }

    private fun authUser(){
        val fName = loginBinding.editTextFirstName.text.toString()
        val uName = loginBinding.editTextUsername.toString()
        if(validateUserInput(fName, loginBinding.inputLayoutFirstName) &&
                validateUserInput(uName, loginBinding.inputLayoutUsername)){
            val chatUser = ChatUser(fName, uName)
            val action = LoginFragmentDirections.actionLoginFragmentToChannelFragment(chatUser)
            findNavController().navigate(action)
        }
    }
    private fun validateUserInput(textInEditText:String, textInLay: TextInputLayout):Boolean{
        return if(textInEditText.length <=3){
            textInLay.isErrorEnabled = true
            textInLay.error = getString(R.string.error)
            false
        }else{
            textInLay.isErrorEnabled = false
            textInLay.error = null
            true
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _loginBinding = null
    }
}