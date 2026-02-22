package com.abstudio.voicenote.data.remote

import okhttp3.*
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    
    // Listener callback
    var onMessageReceived: ((String) -> Unit)? = null
    var onConnectionOpened: (() -> Unit)? = null
    var onConnectionClosed: (() -> Unit)? = null
    var onFailure: ((Throwable) -> Unit)? = null

    fun connect(url: String, token: String) {
        val request = Request.Builder()
            .url(url)
             // Add Auth header if needed, or query param
             .addHeader("Authorization", "Bearer $token")
            .build()
            
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                onConnectionOpened?.invoke()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived?.invoke(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                onConnectionClosed?.invoke()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onFailure?.invoke(t)
            }
        }
        
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    fun sendAudio(data: ByteArray) {
        webSocket?.send(ByteString.of(*data))
    }

    fun close() {
        webSocket?.close(1000, "User stopped recording")
        webSocket = null
        
        // Clear all listeners to prevent late callbacks
        onMessageReceived = null
        onConnectionOpened = null
        onConnectionClosed = null
        onFailure = null
    }
}
