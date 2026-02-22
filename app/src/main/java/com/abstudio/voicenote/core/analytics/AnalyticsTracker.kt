package com.abstudio.voicenote.core.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

object AnalyticsTracker {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    fun trackRecordingStarted() {
        analytics.logEvent("recording_started", null)
    }

    fun trackNoteProcessed(role: String) {
        analytics.logEvent("note_processed", Bundle().apply {
            putString("user_role", role)
        })
    }

    fun trackTaskActionClicked(actionType: String) {
        analytics.logEvent("task_action_clicked", Bundle().apply {
            putString("action_type", actionType)
        })
    }

    fun trackSubscriptionStarted(planName: String) {
        analytics.logEvent("subscription_started", Bundle().apply {
            putString("plan_name", planName)
        })
    }

    fun trackNoteChat(noteId: String) {
        analytics.logEvent("note_chat", Bundle().apply {
            putString("note_id", noteId)
        })
    }

    fun trackScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }
}
