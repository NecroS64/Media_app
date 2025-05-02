package com.example.media_app

import IConnectable
import TcpClient
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter

class PostRepository(
    private val tcpClient: IConnectable
) {

    val responses: Flow<String> = tcpClient.responses.filter { it.contains("send post") or it.contains("authorization") }

    suspend fun connect(serverIp: String, serverPort: Int): Boolean {
        return tcpClient.connect(serverIp, serverPort)
    }

    suspend fun sendMessage(message: String) {
        if (!tcpClient.isConnected()) {
            throw IllegalStateException("Not connected to server")
            Log.d("MyTag_postRep","not connect")
        }
        tcpClient.sendMessage(message)
        Log.d("MyTag_postRep","send message")
    }

    fun isConnected(): Boolean {
        return tcpClient.isConnected()
    }

    fun disconnect() {
        tcpClient.disconnect()
    }
}

class PeopleRepository(
    private val tcpClient: IConnectable
) {

    val responses: Flow<String> = tcpClient.responses.filter { it.contains("send people") }



    suspend fun sendMessage(message: String) {
        if (!tcpClient.isConnected()) {
            throw IllegalStateException("Not connected to server")
        }
        tcpClient.sendMessage(message)
    }

    fun isConnected(): Boolean {
        return tcpClient.isConnected()
    }

    fun disconnect() {
        tcpClient.disconnect()
    }
}

