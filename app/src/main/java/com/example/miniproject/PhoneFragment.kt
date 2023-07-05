package com.example.miniproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.miniproject.databinding.FragmentPhoneBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PhoneFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var flag = false
    private var phno: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentPhoneBinding>(inflater, R.layout.fragment_phone, container, false)
        binding.lifecycleOwner = viewLifecycleOwner // Add this line

        val name = arguments?.getString("name")
        val gotphno = arguments?.getString("getphno")
        val occupation = arguments?.getString("occupation")
        flag = arguments?.getBoolean("flag") == true
        binding.userphno.setText(gotphno)

        binding.getoptbtn.setOnClickListener { view: View ->
            phno = binding.userphno.text.toString()
            if (binding.userphno.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Enter the phone number to verify", Toast.LENGTH_SHORT).show()
            } else {
                val databaseReference = FirebaseDatabase.getInstance().getReference("Profiles")
                val childReference = databaseReference.child("+91"+phno)
                childReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists() || flag==true) {
                            val bundle = Bundle()
                            bundle.putString("getphno", phno)
                            bundle.putString("name", name)
                            bundle.putString("occupation", occupation)
                            view.findNavController().navigate(R.id.action_phoneFragment_to_otpFragment, bundle)
                        } else {
                            Toast.makeText(requireContext(), "Phone Number not registered", Toast.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_phoneFragment_to_signupFragment)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // An error occurred
                        // Handle the error here
                    }
                })
            }
        }

        return binding.root
    }
}
