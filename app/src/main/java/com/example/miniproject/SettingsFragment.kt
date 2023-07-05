package com.example.miniproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.miniproject.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater,R.layout.fragment_settings,container,false)
        firebaseAuth= FirebaseAuth.getInstance()
        binding.logouttxt.setOnClickListener {
            firebaseAuth.signOut()
            it.findNavController().navigate(R.id.action_settingsFragment_to_signupFragment)
        }
        binding.contactus.setOnClickListener {
            val url = "https://linko.page/rvtg7ig3w4iu" // Replace with your desired website URL
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        return binding.root
    }

}