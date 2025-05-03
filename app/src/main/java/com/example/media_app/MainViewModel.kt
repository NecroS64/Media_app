package com.example.media_app

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class STATUS(val code: Int, val value: String){
    green(2,"green"),
    yellow(1,"yellow"),
    red(0,"red");

    companion object {
        fun getCodeByValue(value: String): Int? {
            return entries.find { it.value == value }?.code
        }
    }
}

enum class ROLE(val code: Int, val value: String) {
    VIDEO(2, "v"),
    DESIGNER(1, "d"),
    COPYWRITTER(0, "c");

    companion object {
        fun getCodeByValue(value: String): Int? {
            return entries.find { it.value == value }?.code
        }
    }
}




class Post( var id:Int, var date:String?,
            var time: String, var copywritter: String?, var designer:String?,
            var theme:String?,var add_info:String?, tags:String,var text_status:Int,
            var pict: String?,var pict_status:Int, var id_sheet:Int?){}
class People(var name: String, var role:Int,right:Int?, texrColor: Color?, backColor: Color?, workStatus: Int?){}
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
                        val updatedList = _people.value.orEmpty().toMutableList().apply { add(message.people) }
                        _people.value = updatedList
                    }
                    is IncomingMessage.Unknown -> {
                        Log.w("MyTag_mainViewModel", "Unknown message: ${message.raw}")
                    }

                    is IncomingMessage.Other -> {
                        Log.w("MyTag_mainViewModel", "its other")
                        if (message.json["Command"]== "authorization")
                        {
                            Log.w("MyTag_mainViewModel", "its authorization")
                            if(message.json["Status"]== "successful")
                            {
                                Log.w("MyTag_mainViewModel", "its succesfull")
                                _navigateToDetails.value = Unit
                                send_post()
                                send_people()
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
    fun deletePost(id: Int){
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel","Delete post $id")
                val json = JSONObject()
                json.put("Command", "delete post")
                json.put("id",id)
                messageHandler.send(json.toString())

            } catch (e: Exception) {
                Log.d("MyTag_mainViewModel", "error send")
                Log.d("MyTag_mainViewModel", "Ошибка отправки: ${e.message}")
            }
        }
    }
    fun updatePost(post: Post){
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel","Update post ${post.id}")
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
    fun acceptPost(id: Int){
        viewModelScope.launch {
            try {
                Log.d("MyTag_mainViewModel","complete post $id")
                val json = JSONObject()
                json.put("Command", "complete post")
                json.put("id",id)
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

