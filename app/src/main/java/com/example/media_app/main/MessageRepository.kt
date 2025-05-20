package com.example.media_app.main

import com.example.media_app.api.IConnectable
import android.util.Log
import com.example.media_app.api.PeoplePostCount
import com.example.media_app.api.PeopleTable
import com.example.media_app.api.PostDAO
import com.example.media_app.api.PostTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

class PostRepository(
    private val tcpClient: IConnectable,
    private val bd : PostDAO
) {

    val responsesWEB: Flow<String> = tcpClient.responses//.filter { it.contains("send post") or it.contains("authorization") }
    val responsesBDPost: Flow<List<PostTable>> = bd.getAllPost()
    suspend fun connect(serverIp: String, serverPort: Int): Boolean {
        return tcpClient.connect(serverIp, serverPort)
    }
    suspend fun addPost(post: PostTable)
    {
        bd.insertPostAndUpdatePeople(post)
    }
    suspend fun dellAllPost(){
        bd.deleteAllPost()
    }
    suspend fun deletePost(id:Int){
        bd.deletePost(id)
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
    private val tcpClient: IConnectable,
    private val bd : PostDAO
) {

    val responsesWEB: Flow<String> = tcpClient.responses.filter { it.contains("send people") }
    val responsesBDPeople: Flow<List<PeopleTable>> = bd.getAllPeople()

    suspend fun addPeople(people: PeopleTable)
    {
        bd.Insert(people)
    }
    suspend fun sendMessage(message: String) {
        if (!tcpClient.isConnected()) {
            throw IllegalStateException("Not connected to server")
        }
        tcpClient.sendMessage(message)
    }

    suspend fun GetPeopleCostCount(switchState:Int,spinnerValue:Int):List<PeoplePostCount>{
        Log.d("MyTag_peopleRep","getpeopleposcount")
        return bd.getPeoplePostCounts(switchState,spinnerValue)
    }

    fun isConnected(): Boolean {
        return tcpClient.isConnected()
    }

    fun disconnect() {
        tcpClient.disconnect()
    }
}

