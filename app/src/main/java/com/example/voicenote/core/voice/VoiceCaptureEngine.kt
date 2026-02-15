package com.example.voicenote.core.voice

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VoiceCaptureEngine(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var currentFile: File? = null

    fun startRecording(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.filesDir, "VOICE_$timestamp.mp3")
        currentFile = file

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFile(file.absolutePath)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }
        return file
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun getAmplitude(): Int {
        return mediaRecorder?.maxAmplitude ?: 0
    }
}
