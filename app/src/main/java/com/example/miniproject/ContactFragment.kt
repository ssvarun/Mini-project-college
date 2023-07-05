package com.example.miniproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.miniproject.databinding.FragmentContactBinding
import com.example.miniproject.databinding.FragmentShowProfileBinding

class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentContactBinding>(inflater, R.layout.fragment_contact, container, false)
        binding.contacttxt.text= arguments?.getString("profilecontact")
        return binding.root
    }

}