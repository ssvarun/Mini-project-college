package com.example.miniproject

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var homeFragment: HomeFragment
    lateinit var sendtoken: String

    override fun onNewToken(token: String) {
        sendtoken = token // Initialize the sendtoken property with the received token
        saveTokenToUserProfile(token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming messages if needed
    }

    private fun saveTokenToUserProfile(token: String) {

        // Get the current user's ID or any unique identifier

        // Check if HomeFragment is initialized
    }
    fun getfcmfromsignup(){
        onNewToken(token = String.toString())
    }

}

