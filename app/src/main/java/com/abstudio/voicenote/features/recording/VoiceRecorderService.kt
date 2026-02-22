package com.abstudio.voicenote.features.recording

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.abstudio.voicenote.R
import com.abstudio.voicenote.data.remote.WebSocketClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class VoiceRecorderService : Service() {

    companion object {
        private val _isRecordingFlow = MutableStateFlow(false)
        val isRecordingFlow: StateFlow<Boolean> = _isRecordingFlow.asStateFlow()
    }

    @Inject
    lateinit var webSocketClient: WebSocketClient

    @Inject
    lateinit var noteRepository: com.abstudio.voicenote.data.repository.NoteRepository

    private val binder = LocalBinder()
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val serviceScopeJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceScopeJob)
    private var currentFile: File? = null
    private var outputStream: FileOutputStream? = null

    inner class LocalBinder : Binder() {
        fun getService(): VoiceRecorderService = this@VoiceRecorderService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            "START_RECORDING" -> {
                val filename = "recording_${System.currentTimeMillis()}"
                startRecording(filename)
            }
            "STOP_RECORDING" -> {
                stopRecording()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    fun startRecording(fileName: String) {
        if (isRecording) return
        com.abstudio.voicenote.core.analytics.AnalyticsTracker.trackRecordingStarted()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission should be checked by UI before starting service
            return
        }

        startForeground(1, createNotification())

        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true
        _isRecordingFlow.value = true

        val outputFile = File(filesDir, "$fileName.pcm")
        currentFile = outputFile

        // WebSocket Connect (Assume token is passed or injected? For now mock token)
        // webSocketClient.connect("wss://api.voicenote.com/api/v1/audio/stream", "mock_token")

        recordingJob = serviceScope.launch {
            val buffer = ByteArray(bufferSize)
            outputStream = FileOutputStream(outputFile)

            try {
                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        outputStream?.write(buffer, 0, read)
                        // webSocketClient.sendAudio(buffer.copyOf(read)) // Stream
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    outputStream?.close()
                    outputStream = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // webSocketClient.close()
            }
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        isRecording = false
        _isRecordingFlow.value = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        recordingJob?.cancel()
        
        // Clean up streams
        try {
            outputStream?.close()
            outputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        stopForeground(STOP_FOREGROUND_REMOVE)

        // Process and Upload
        serviceScope.launch {
            currentFile?.let { pcmFile ->
                if (pcmFile.exists() && pcmFile.length() > 0) {
                    val wavFile = File(filesDir, "${pcmFile.nameWithoutExtension}.wav")
                    // Convert PCM to WAV
                    com.abstudio.voicenote.core.util.WavUtils.pcmToWav(pcmFile, wavFile)
                    
                    // Upload
                    showProcessingNotification()
                    val result = noteRepository.uploadVoiceNote(wavFile)
                    if (result is com.abstudio.voicenote.core.util.NetworkResult.Success) {
                        showSuccessNotification()
                    } else {
                        showErrorNotification(result.message ?: "Upload Failed")
                    }
                    
                    // Cleanup
                    pcmFile.delete() 
                    // wavFile.delete() // Keep or delete based on preference? Maybe keep for a bit or cache.
                    // For now, let's keep it as local cache until synced properly
                }
            }
        }
    }

    private fun showProcessingNotification() {
        val notification = NotificationCompat.Builder(this, "VoiceNoteRecording")
            .setContentTitle("Processing Voice Note")
            .setContentText("Uploading...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, notification)
    }

    private fun showSuccessNotification() {
        val notification = NotificationCompat.Builder(this, "VoiceNoteRecording")
            .setContentTitle("Voice Note Saved")
            .setContentText("Upload complete.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.cancel(1) // Cancel recording notif
        manager.cancel(2) // Cancel processing notif
        manager.notify(3, notification)
    }

    private fun showErrorNotification(error: String) {
        val notification = NotificationCompat.Builder(this, "VoiceNoteRecording")
            .setContentTitle("Upload Failed")
            .setContentText(error)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.cancel(1)
        manager.cancel(2)
        manager.notify(4, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure recording is stopped
        stopRecording()
        
        // Cancel all coroutines
        serviceScopeJob.cancel()
        
        // Clean up any remaining resources
        try {
            outputStream?.close()
            outputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "VoiceNoteRecording",
                "Voice Note Recording",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "VoiceNoteRecording")
            .setContentTitle("Recording Voice Note")
            .setContentText("Listening...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with valid icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
