package com.abstudio.voicenote.features.player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioPlayerUiState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentUrl: String? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val playbackSpeed: Float = 1.0f,
    val error: String? = null
)

@HiltViewModel
class AudioPlayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    fun play(url: String) {
        if (_uiState.value.currentUrl == url) {
            resume()
            return
        }

        releasePlayer()
        _uiState.update { it.copy(isLoading = true, currentUrl = url, error = null) }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { mp ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            duration = mp.duration.toLong(),
                            isPlaying = true
                        ) 
                    }
                    start()
                    startProgressTracking()
                }
                setOnCompletionListener {
                    _uiState.update { it.copy(isPlaying = false, progress = 1f, currentPosition = it.duration) }
                    stopProgressTracking()
                }
                setOnErrorListener { _, _, _ ->
                    _uiState.update { it.copy(isLoading = false, isPlaying = false, error = "Playback Error") }
                    stopProgressTracking()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _uiState.update { it.copy(isPlaying = false) }
            stopProgressTracking()
        }
    }

    private fun resume() {
        if (mediaPlayer != null && !_uiState.value.isPlaying) {
            mediaPlayer?.start()
            _uiState.update { it.copy(isPlaying = true) }
            startProgressTracking()
        }
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun toggleSpeed() {
        val currentSpeed = _uiState.value.playbackSpeed
        val nextSpeed = when (currentSpeed) {
            1.0f -> 1.5f
            1.5f -> 2.0f
            2.0f -> 0.5f
            else -> 1.0f
        }
        
        _uiState.update { it.copy(playbackSpeed = nextSpeed) }
        
        mediaPlayer?.let { mp ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                try {
                    val params = mp.playbackParams
                    params.speed = nextSpeed
                    mp.playbackParams = params
                } catch (e: Exception) {
                    // Fallback or ignore if not supported by current state
                }
            }
        }
    }

    fun seekTo(position: Float) {
        mediaPlayer?.let { mp ->
            val seekPos = (position * mp.duration).toInt()
            mp.seekTo(seekPos)
            _uiState.update { it.copy(progress = position, currentPosition = seekPos.toLong()) }
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = viewModelScope.launch {
            while (true) {
                if (mediaPlayer?.isPlaying == true) {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val total = mediaPlayer?.duration ?: 1
                    if (total > 0) {
                        val progress = current.toFloat() / total.toFloat()
                        _uiState.update { 
                            it.copy(progress = progress, currentPosition = current.toLong()) 
                        }
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopProgressTracking()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
