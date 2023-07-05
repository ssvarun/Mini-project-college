package com.example.miniproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.chaos.view.PinView
import com.example.miniproject.databinding.FragmentOtpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit




class OtpFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mPhoneAuthProvider:PhoneAuthProvider
    private lateinit var codeBySystem:String
    private lateinit var pinFromUser:PinView
    private var flag=false
    private lateinit var phno:String
    private lateinit var bundle:Bundle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentOtpBinding>(inflater, R.layout.fragment_otp, container, false)
        phno = "+91"+arguments?.getString("getphno")

        val name= arguments?.getString("name")
        val occupation = arguments?.getString("occupation")

        mAuth = FirebaseAuth.getInstance()
        mPhoneAuthProvider = PhoneAuthProvider.getInstance()
        sendVerificationCodeToUser(phno)

        pinFromUser = binding.pinView

        binding.verifyotpbtn.setOnClickListener { view:View->
            bundle = Bundle()
            bundle.putString("getphno",phno)
            bundle.putString("name",name)
            bundle.putString("occupation",occupation)
            val userotp = pinFromUser.text.toString()
            verifyCode(userotp)
        }
        return binding.root
    }

    private fun sendVerificationCodeToUser(phno: String?) {
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth) //mAuth is defined on top
            .setPhoneNumber(phno!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            codeBySystem = s
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            // Do not call verifyCode() here
            val code = phoneAuthCredential.smsCode
            code?.let {
                pinFromUser.setText(it)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(codeBySystem, code)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(),
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        flag=true
                        Toast.makeText(requireContext(),"OTP successful",Toast.LENGTH_SHORT).show()
                        if(flag==true) {
                            view?.findNavController()
                                ?.navigate(R.id.action_otpFragment_to_homeFragment, bundle)
                        }
                    } else {
                        flag=false
                        Toast.makeText(requireContext(),"OTP verification failed. Please try again.",Toast.LENGTH_SHORT).show()
                    }
                })
    }

}


