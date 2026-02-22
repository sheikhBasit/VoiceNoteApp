package com.abstudio.voicenote.data.service

import com.abstudio.voicenote.data.local.TokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to preference
        // ideally we also sync it to backend immediately if user is logged in
        // For now, saving to TokenManager is enough, AuthViewModel picks it up (via dummy currently)
        // I should update "dummy_fcm_token" in AuthViewModel later to use this.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Handle data payload: message.data
        // Handle notification payload: message.notification
        // With SSE, we might rely less on FCM for app-open updates, but FCM is key for background.
        // For MVP, just logging or showing notification is okay.
    }
}
