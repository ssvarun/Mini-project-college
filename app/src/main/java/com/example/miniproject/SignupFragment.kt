package com.example.miniproject

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.miniproject.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.coroutineScope

class SignupFragment : Fragment() {
// Get a reference to the SharedPreferences object
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var getfcm: MyFirebaseMessagingService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        getfcm = MyFirebaseMessagingService()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

    val binding = DataBindingUtil.inflate<FragmentSignupBinding>(inflater,R.layout.fragment_signup,container,false)
        binding.signupbtn.setOnClickListener{ view:View->
            val name = binding.name.text.toString()
            val phno = binding.phno.text.toString()
            val occupation = binding.occupation.text.toString()
            val bundle = Bundle()
            bundle.putString("name",name)
            bundle.putString("getphno",phno)
            bundle.putString("occupation",occupation)
            bundle.putBoolean("flag",true)
            checkIfPhoneNumberExists("+91"+phno)
            if(name.isNotEmpty() && phno.isNotEmpty() && occupation.isNotEmpty() && phno.length==10)
            view.findNavController().navigate(R.id.action_signupFragment_to_phoneFragment,bundle)
        }
        binding.signinbtn.setOnClickListener { view:View->
            view.findNavController().navigate(R.id.action_signupFragment_to_phoneFragment)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val phno = firebaseAuth.currentUser?.phoneNumber
        val bundle = Bundle()
        bundle.putString("getphno",phno)
        if(firebaseAuth.currentUser!=null)
        {
            findNavController().navigate(R.id.action_signupFragment_to_homeFragment,bundle)
//            onDestroy()
        }
    }
    private fun checkIfPhoneNumberExists(phno: String) {
        firebaseAuth.fetchSignInMethodsForEmail(phno)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.contains(PhoneAuthProvider.PROVIDER_ID)) {
                        Toast.makeText(requireContext(),"You have already registered\nPlease sign in",Toast.LENGTH_SHORT).show()
                    } else {
                        // Phone number not registered
                        // Proceed with the registration process
                    }
                } else {
                    // Error occurred while checking phone number existence
                    // Handle the error
                }
            }
    }

}