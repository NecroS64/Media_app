package com.example.media_app

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.media_app.api.AppDatabase
import com.example.media_app.api.PeoplePostCount
import com.example.media_app.api.PeopleTable
import com.example.media_app.api.PostTable
import com.example.media_app.api.WebSocketClient
import com.example.media_app.main.IncomingMessage
import com.example.media_app.main.MessageHandler
import com.example.media_app.main.PeopleRepository
import com.example.media_app.main.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


val ip_local = "192.168.1.112"
val ip_serverD = "138.124.109.92"
val ip_serverS = "77.239.102.17"
val ip_me = "192.168.2.114"


enum class STATUS(val code: Int, val value: String) {
    green(2, "green"),
    yellow(1, "yellow"),
    red(0, "red");

    companion object {
        fun getCodeByValue(value: String): Int? {
            return entries.find { it.value == value }?.code
        }
    }
}

enum class ROLE(val code: Int, val value: String) {
    VIDEO(2, "v"),
    DESIGNER(1, "d"),
    COPYWRITTER(0, "k");

    companion object {
        fun getCodeByValue(value: String): Int? {
            return entries.find { it.value == value }?.code
        }
    }
}


class Post(
    var id: Int, var date: String?,
    var time: String, var copywritter: String?, var designer: String?,
    var theme: String?, var add_info: String?, var tags: String, var text_status: Int,
    var pict: String?, var pict_status: Int
) {}

class People(
    var name: String,
    var role: Int,
    right: Int?,
    texrColor: Color?,
    backColor: Color?,
    workStatus: Int?
) {}

class MainViewModel (application: Application) : AndroidViewModel(application) {
    private val serverIp = ip_serverD  // Заменить на нужный IP
    private val serverPort = 3749

    private val client = WebSocketClient()
    private val database = AppDatabase.getInstance(application).postDao()
    private val postRepository = PostRepository(client, database)
    private val peopleRepository = PeopleRepository(client, database)
    private val messageHandler = MessageHandler(postRepository, peopleRepository)

    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> = _connectionState

    private val _navigateToDetails = MutableLiveData<Unit>()
    val navigateToDetails: LiveData<Unit> = _navigateToDetails

    private val _posts = MutableLiveData<List<PostTable>>()
    val posts: LiveData<List<PostTable>> get() = _posts

    private val _people = MutableLiveData<List<PeopleTable>>()
    val people: LiveData<List<PeopleTable>> get() = _people

    init {
        observeResponses()
        connectToServer()
        observeConnectionState()
    }

    private fun observeResponses() {
        viewModelScope.launch {
            messageHandler.observeMessages().collect { message ->
                when (message) {
                    is IncomingMessage.PostsMessage -> {
                        _posts.value = message.posts
                    }

                    is IncomingMessage.PeoplesMessage -> {
                        _people.value = message.peoples
                    }

                    is IncomingMessage.Unknown -> {
                        Log.d("MyTag_MainViewModel", "Unknown message: ${message.raw}")
                    }

                    is IncomingMessage.Other -> {
                        if (message.json["Command"] == "authorization") {
                            if (message.json["Status"] == "successful") {
                                _navigateToDetails.value = Unit
                                //sendPost()
                                sendPeople()
                            }
                        }
                    }
                    is IncomingMessage.EndMessage  -> {
                        if (message.command=="send people end")
                            sendPost()
                    }
                    // Обработчики можно добавить по мере необходимости
                    is IncomingMessage.PostMessage -> {}
                    is IncomingMessage.err -> {}
                    is IncomingMessage.PeopleMessage -> {}
                }
            }
        }
    }

    private fun connectToServer() {
        viewModelScope.launch {
            val connected = messageHandler.connect(serverIp, serverPort)
            _connectionState.postValue(connected)
        }
    }

    fun changeServer(ip: String) {
        viewModelScope.launch {
            try {
                messageHandler.disconnect()
                val connected = messageHandler.connect(ip, serverPort)
                _connectionState.postValue(connected)
            } catch (e: Exception) {
                Log.d("MyTag_MainViewModel", "Ошибка смены сервера: ${e.message}")
            }
        }
    }

    fun auth(name: String) {
        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("Command", "authorization")
                    put("Name", name)
                }
                messageHandler.send(json.toString())
            } catch (e: Exception) {
                Log.d("MyTag_MainViewModel", "Ошибка авторизации: ${e.message}")
            }
        }
    }

    fun sendPost() {
        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("Command", "send post")
                    put("Filter", "Null")
                }
                messageHandler.send(json.toString())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка отправки постов: ${e.message}")
            }
        }
    }

    fun sendPeople() {
        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("Command", "send people")
                    put("Filter", "Null")
                }
                messageHandler.send(json.toString())
            } catch (e: Exception) {
                Log.d("MainViewModel", "Ошибка отправки людей: ${e.message}")
            }
        }
    }

    private fun observeConnectionState() {
        connectionState.observeForever { isConnected ->
            if (isConnected == false) {
                Log.d("MyTag_MainViewModel", "Connection lost. Reconnecting...")
                viewModelScope.launch {
                    reconnectWithRetry()
                }
            }
        }
    }


    suspend fun getPeopleCostCount(switchState: Int, spinnerValue: Int): List<PeoplePostCount> {
        return messageHandler.GetPeopleCostCount(switchState, spinnerValue)
    }

    fun saveUser(isLoggedIn: Boolean) {
        val prefs = getApplication<Application>()
            .getSharedPreferences("app_cache", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("user_logged_in", isLoggedIn).apply()
    }

    fun loadUser(): Boolean {
        val prefs = getApplication<Application>()
            .getSharedPreferences("app_cache", Context.MODE_PRIVATE)
        return prefs.getBoolean("user_logged_in", false)
    }

    fun addPost(post: PostTable) {
        val updatedList = _posts.value.orEmpty().toMutableList().apply { add(post) }
        _posts.value = updatedList
    }



    private suspend fun reconnectWithRetry() {
        val maxAttempts = 5
        var attempt = 0
        val delayMillis = 3000L

        while (_connectionState.value == false && attempt < maxAttempts) {
            delay(delayMillis)
            val connected = messageHandler.connect(serverIp, serverPort)
            _connectionState.postValue(connected)
            attempt++
            if (connected) {
                Log.d("MyTag_MainViewModel", "Reconnected successfully on attempt $attempt")
                break
            } else {
                Log.d("MyTag_MainViewModel", "Reconnect attempt $attempt failed")
            }
        }
    }




    fun autho(name: String) {
        viewModelScope.launch {
            try {
                val json = JSONObject()
                json.put("Command", "authorization")
                json.put("Name", name)
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun change_server(ip: String) {
        viewModelScope.launch {
            try {
                messageHandler.disconnect()
                val serverPort = 3749         // Порт сервера
                messageHandler.connect(ip, serverPort)
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun sendMessage(msg: String) {
        viewModelScope.launch {
            try {
                messageHandler.send(msg)
                Log.d("MyTag_mainViewModel", "send")
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun updatePostList() {
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel", "its a swipe")
                val json = JSONObject()
                json.put("Command", "update database")
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun deletePost(id: Int) {
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel", "Delete post $id")
                val json = JSONObject()
                json.put("Command", "delete post")
                json.put("id", id)
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun deleteAllPost() {
        viewModelScope.launch {
            try {
                messageHandler.dellAllPost()
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel", "Update post ${post.id}")
                val json = JSONObject()
                json.put("Command", "update post")
                //TODO
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun acceptPost(id: Int) {
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel", "complete post $id")
                val json = JSONObject()
                json.put("Command", "complete post")
                json.put("id", id)
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun disconnect() {
        messageHandler.disconnect()
    }

    fun send_people() {
        viewModelScope.launch {
            try {
                val json = JSONObject()
                json.put("Command", "send people")
                messageHandler.send(json.toString())
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }


}

