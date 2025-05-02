package com.example.media_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class STATUS(val value:Int){
    green(2),
    yellow(1),
    red(0)

}


class Post( var id:Int, var date:String?,
            var time: String, var copywritter: String?, var designer:String?,
            var theme:String?,var add_info:String?, tags:String,var text_status:Int,
            var pict: String?,var pict_status:Int, var id_sheet:Int){}
class People(){}
class MainViewModel(

) : ViewModel() {
    private lateinit var  messageHandler: MessageHandler


    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> = _connectionState

    private val _navigateToDetails = MutableLiveData<Unit>()
    val navigateToDetails: LiveData<Unit> get() = _navigateToDetails

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _people = MutableLiveData<List<People>>()
    val people: LiveData<List<People>> get() = _people

    fun addPost(post: Post) {
        val updatedList = _posts.value.orEmpty().toMutableList().apply { add(post) }
        _posts.value = updatedList
    }


    fun setHandler(messageHandler: MessageHandler)
    {
        this.messageHandler=messageHandler
        observeResponses()
    }
    private fun observeResponses() {
        viewModelScope.launch {
            messageHandler.observeMessages().collect { message ->
                when (message) {
                    is IncomingMessage.PostMessage -> {
                        val updatedList = _posts.value.orEmpty().toMutableList().apply { add(message.post) }
                        _posts.value = updatedList
                    }
                    is IncomingMessage.PeopleMessage -> {
                        // обработка people
                    }
                    is IncomingMessage.Unknown -> {
                        Log.w("MyTag_mainViewModel", "Unknown message: ${message.raw}")
                    }

                    is IncomingMessage.Other -> {
                        Log.w("MyTag_mainViewModel", "its other")
                        if (message.json["Command"]== "authorization")
                        {
                            Log.w("MyTag_mainViewModel", "its authorization")
                            if(message.json["Answer"]== "successful")
                            {
                                Log.w("MyTag_mainViewModel", "its succesfull")
                                _navigateToDetails.value = Unit
                            }
                            else
                            {

                            }
                        }
                    }
                }
            }
        }
    }

    fun connectToServer(serverIp: String, serverPort: Int) {
        viewModelScope.launch {
            val connected = messageHandler.connect(serverIp, serverPort)
            _connectionState.postValue(connected)
        }
    }


    fun send_post() {
        viewModelScope.launch {
            try {
                val json = JSONObject()
                json.put("Command", "send post")
                json.put("Filter", "Null")
                messageHandler.send(json.toString())
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }

    fun autho(name: String)
    {
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

    fun change_server(ip: String )
    {
        viewModelScope.launch {
            try {
        messageHandler.disconnect()
        val serverPort = 3749         // Порт сервера
        messageHandler.connect(ip,serverPort)
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
                Log.d("MyTag_mainViewModel","send")
            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel","error send")
                Log.d("MyTag_mainViewModel","Ошибка отправки: ${e.message}")
            }
        }
    }
    fun updatePostList()
    {
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel","its a swipe")
                val json = JSONObject()
                json.put("Command", "update database")
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





}

