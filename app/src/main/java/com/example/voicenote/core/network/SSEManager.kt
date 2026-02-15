package com.example.voicenote.core.network

import com.example.voicenote.utils.json_logger.JLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

/**
 * Foundation for SSE connection to /sse/events
 */
class SSEManager(private val okHttpClient: OkHttpClient) {

    private var eventSource: EventSource? = null

    fun connect(url: String, listener: SSEListener) {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                listener.onOpen()
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                listener.onEvent(type, data)
            }

            override fun onClosed(eventSource: EventSource) {
                listener.onClosed()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                listener.onError(t)
            }
        })
    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
    }

    interface SSEListener {
        fun onOpen()
        fun onEvent(type: String?, data: String)
        fun onClosed()
        fun onError(t: Throwable?)
    }
}
