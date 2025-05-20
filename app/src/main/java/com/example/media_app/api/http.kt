package com.example.media_app.api

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString

class WebSocketClient : IConnectable {

    private var webSocket: WebSocket? = null
    private var isConnectedInternal = false

    private val _responses = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val responses: SharedFlow<String> = _responses.asSharedFlow()

    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var connectionResult = CompletableDeferred<Boolean>()

    override suspend fun connect(serverIp: String, serverPort: Int): Boolean = withContext(Dispatchers.IO) {
        Log.d("MyTag_WS", "connect to \"ws://$serverIp:$serverPort\"")
        val url = "ws://$serverIp:$serverPort"
        val request = Request.Builder().url(url).build()

        connectionResult = CompletableDeferred()

        return@withContext try {
            val listener = SocketListener()
            webSocket = client.newWebSocket(request, listener)
            connectionResult.await()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MyTag_WS", "not connect (exception)")
            false
        }
    }


    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        if (!isConnectedInternal) throw IllegalStateException("WebSocket is not connected")
        Log.d("MyTag_WS","mes: $message")
        webSocket?.send(message)
    }

    override fun isConnected(): Boolean = isConnectedInternal

    override fun disconnect() {
        isConnectedInternal = false
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
    }

    private inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnectedInternal = true
            Log.d("MyTag_WS", "open connection")
            if (!connectionResult.isCompleted) {
                connectionResult.complete(true)
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            scope.launch {
                Log.d("MyTag_WS","receive ${text}")
                _responses.emit(text)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // Преобразуем бинарные сообщения в строку, если нужно
            scope.launch {
                Log.d("MyTag_WS","receive ${bytes.utf8()}")
                _responses.emit(bytes.utf8())
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            isConnectedInternal = false
            webSocket.close(code, reason)
            Log.d("MyTag_WS","close connection")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnectedInternal = false
            Log.d("MyTag_WS", "failure connection")

            if (!connectionResult.isCompleted) {
                connectionResult.complete(false)
            }
        }
    }
}

class TcpClient: IConnectable {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    @Volatile private var isConnected = false

    private val _responses = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val responses: SharedFlow<String> = _responses.asSharedFlow()

    override suspend  fun connect( serverIp: String, serverPort: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            socket = Socket(serverIp, serverPort)
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            isConnected = true
            listenForResponses()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            isConnected = false
            false
        }
    }



    private fun listenForResponses() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (isConnected) {
                    val response = reader?.readLine()
                    if (response != null) {
                        _responses.emit(response)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isConnected = false
            }
        }
    }

    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        if (isConnected) {
            try {
                writer?.println(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            throw IllegalStateException("Not connected to server")
        }
    }

    override fun isConnected(): Boolean = isConnected

    override fun disconnect() {
        try {
            isConnected = false
            socket?.shutdownOutput()
            socket?.shutdownInput()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface IConnectable{
    suspend fun connect(serverIp: String, serverPort: Int): Boolean
    fun disconnect()
    suspend fun sendMessage(message: String):Unit
    fun isConnected(): Boolean
    val responses:SharedFlow<String>
}

