package com.example.media_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.sql.Time
import java.util.Date






//class PostViewModel(
//    private val handler: MessageHandler
//) : ViewModel() {
//
//    private val _posts = MutableLiveData<List<Post>>(emptyList())
//    val posts: LiveData<List<Post>> get() = _posts
//
//    init {
//        collectMessagesFromServer()
//    }
//
//    private fun collectMessagesFromServer() {
//        viewModelScope.launch {
//            handler.observeRawMessages().collect { raw ->
//                try {
//                    val json = JSONObject(raw)
//                    if (json.getString("type") == "new_post") {
//                        val post = parsePost(json)
//                        addPost(post)
//                    }
//                } catch (e: Exception) {
//                    Log.e("PostViewModel", "Ошибка парсинга JSON: ${e.message}")
//                }
//            }
//        }
//    }
//
//    private fun parsePost(json: JSONObject): Post {
//        return Post(
//            id = json.getInt("id"),
//            date = json.optString("date"),
//            time = json.getString("time"),
//            copywritter = json.optString("copywritter"),
//            designer = json.optString("designer"),
//            theme = json.optString("theme"),
//            add_info = json.optString("add_info"),
//            tags = json.optString("tags"),
//            text_status = json.optInt("text_status"),
//            pict = json.optString("pict"),
//            pict_status = json.optInt("pict_status"),
//            id_sheet = json.optInt("id_sheet")
//        )
//    }
//
//    fun addPost(post: Post) {
//        val current = _posts.value ?: emptyList()
//        if (current.none { it.id == post.id }) {
//            _posts.value = current + post
//        }
//    }
//
//    fun sendPostToServer(post: Post) {
//        val json = JSONObject().apply {
//            put("type", "new_post")
//            put("id", post.id)
//            put("date", post.date)
//            put("time", post.time)
//            put("copywritter", post.copywritter)
//            put("designer", post.designer)
//            put("theme", post.theme)
//            put("add_info", post.add_info)
//            put("tags", post.tags)
//            put("text_status", post.text_status)
//            put("pict", post.pict)
//            put("pict_status", post.pict_status)
//            put("id_sheet", post.id_sheet)
//        }
//
//        viewModelScope.launch {
//            try {
//                handler.send(json.toString())
//            } catch (e: Exception) {
//                Log.e("PostViewModel", "Ошибка отправки: ${e.message}")
//            }
//        }
//    }
//
//    fun connectToServer() {
//        viewModelScope.launch {
//            val connected = handler.connect()
//            Log.d("PostViewModel", "TCP Connected: $connected")
//        }
//    }
//
//    fun disconnect() = handler.disconnect()
//}
