package com.example.media_app


import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.json.JSONObject


sealed class IncomingMessage {
    data class PostMessage(val post: Post): IncomingMessage()
    data class PeopleMessage(val people: People): IncomingMessage()
    data class Unknown(val raw: String): IncomingMessage()
    data class Other(val json: JSONObject): IncomingMessage()
}


class MessageHandler(
    private val postRepository: PostRepository,
    private val peopleRepository: PeopleRepository
) {
    suspend fun connect(serverIp: String, serverPort: Int): Boolean {
        return postRepository.connect(serverIp, serverPort)
    }

    suspend fun send(message: String) {
        postRepository.sendMessage(message)
        Log.d("MyTag_handler","send message")
    }

    fun observeMessages(): Flow<IncomingMessage> {
        val postFlow = postRepository.responses.map {
            try {
                if (it.contains("authorization")) {
                    Log.d("MyTag_handler", "get authorization")
                    IncomingMessage.Other(JSONObject(it))
                }
                else {
                    Log.d("MyTag_handler", "get post")
                    IncomingMessage.PostMessage(parsePost(JSONObject(it).getJSONObject("Post values")))
                }
            } catch (e: Exception) {
                e.message?.let { it1 -> Log.d("MyTag_handler", it1) }
                if (it.contains("authorization"))
                    IncomingMessage.Other(JSONObject(it))
                else
                    IncomingMessage.Unknown(it)
            }
        }

        val peopleFlow = peopleRepository.responses.map {
            try {
                IncomingMessage.PeopleMessage(parsePeople(JSONObject(it)))
            } catch (e: Exception) {
                IncomingMessage.Unknown(it)
            }
        }

        return merge(postFlow, peopleFlow)
    }
    private fun parsePost(json: JSONObject): Post {
        Log.d("MyTag_handler", "parsing message")

        val id = json.getInt("id")
        val date = json.optString("date")
        val time = json.getString("time")
        val copywritter = json.getJSONArray("kop").getString(0)
        val designer = json.getJSONArray("dis").getString(0)
        val theme = json.optString("theme")
        val addInfo = json.optString("add_inf")
        val tags = json.getJSONArray("tegs").getString(0)
        val textStatus = json.optInt("text_status")
        val pict = json.optString("pict")
        val pictStatus = json.optInt("pict_status")
        val idSheet = json.optInt("id_sheet")

        return Post(
            id = id,
            date = date,
            time = time,
            copywritter = copywritter,
            designer = designer,
            theme = theme,
            add_info = addInfo,
            tags = tags,
            text_status = textStatus,
            pict = pict,
            pict_status = pictStatus,
            id_sheet = idSheet
        )

    }
    private fun parsePeople(json: JSONObject): People {
        return People(

        )
    }

    fun disconnect() {
        postRepository.disconnect()
    }
}

