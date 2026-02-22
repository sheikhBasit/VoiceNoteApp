package com.abstudio.voicenote.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.local.dao.NotificationDao
import com.abstudio.voicenote.data.models.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationDao: NotificationDao
) : ViewModel() {

    val notifications = notificationDao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationDao.markAllAsRead()
        }
    }

    fun dismissNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            // In a real app, might delete or mark as dismissed
            notificationDao.updateNotification(notification.copy(isRead = true))
        }
    }
}
