package com.example.media_app


import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.json.JSONObject


sealed class IncomingMessage {
    data class PostsMessage(val posts: List<PostTable>): IncomingMessage()
    data class PostMessage(val post: String): IncomingMessage()
    data class PeopleMessage(val people: People): IncomingMessage()
    data class PeoplesMessage(val peoples: List<PeopleTable>): IncomingMessage()
    data class Unknown(val raw: String): IncomingMessage()
    data class Other(val json: JSONObject): IncomingMessage()
    data class err(val json: JSONObject): IncomingMessage()
}


class MessageHandler(
    private val postRepository: PostRepository,
    private val peopleRepository: PeopleRepository
) {
    suspend fun connect(serverIp: String, serverPort: Int): Boolean {
        return postRepository.connect(serverIp, serverPort)
    }
    suspend fun dellAllPost(){
        postRepository.dellAllPost()
    }
    suspend fun send(message: String) {
        postRepository.sendMessage(message)
        Log.d("MyTag_handler","send message")
    }
    suspend fun GetPeopleCostCount(switchState:Int,spinnerValue:Int):List<PeoplePostCount>{
        return peopleRepository.GetPeopleCostCount(switchState,spinnerValue)
    }

    fun observeMessages(): Flow<IncomingMessage> {
        val postFlow = postRepository.responsesWEB.map {
            try {
                val command = JSONObject(it).optString("Command")
                if (it.contains("authorization")) {
                    Log.d("MyTag_handler", "get authorization")
                    IncomingMessage.Other(JSONObject(it))
                }
                else if(command == "send post") {
                    Log.d("MyTag_handler", "get post")
                    postRepository.addPost(parsePost(JSONObject(it).getJSONObject("Post values")))
                     IncomingMessage.Unknown("new post to db")
                    //IncomingMessage.PostMessage(parsePost(JSONObject(it).getJSONObject("Post values")).toString())
                    //IncomingMessage.PostsMessage(it)
                }
                else if(command == "synchronization") {
                    Log.d("MyTag_handler", "synchronization")
                    postRepository.addPost(parsePost(JSONObject(it).getJSONObject("Post values")))
                    IncomingMessage.Unknown("synchronization")
                    //IncomingMessage.PostMessage(parsePost(JSONObject(it).getJSONObject("Post values")).toString())
                    //IncomingMessage.PostsMessage(it)
                }
                else if(command == "delete post") {
                    Log.d("MyTag_handler", "delete post answer")
                    val id = JSONObject(it).optInt("id");
                    postRepository.deletePost(id)
                    IncomingMessage.Unknown("delete post")
                    //IncomingMessage.PostMessage(parsePost(JSONObject(it).getJSONObject("Post values")).toString())
                    //IncomingMessage.PostsMessage(it)
                }
                else {
                    IncomingMessage.Unknown("unknow message $it")
                }
            } catch (e: Exception) {
                e.message?.let { it1 -> Log.d("MyTag_handler", it1) }
                if (it.contains("authorization"))
                    IncomingMessage.Other(JSONObject(it))
                else
                    IncomingMessage.Unknown(it)
            }
        }

        val peopleFlow = peopleRepository.responsesWEB.map {
            try {
                peopleRepository.addPeople(parsePeople(JSONObject(it).getJSONObject("People values")))
                IncomingMessage.Unknown("new people to db")
                //IncomingMessage.PeopleMessage(parsePeople(JSONObject(it).getJSONObject("People values")))

            } catch (e: Exception) {
                IncomingMessage.Unknown(it)
            }
        }
        val peopleDatabaseFlow = peopleRepository.responsesBDPeople.map {
            try {
                IncomingMessage.PeoplesMessage(it)
            } catch (e: Exception) {
                IncomingMessage.Unknown("error ${e.message}")
            }
        }
        val postDatabaseFlow = postRepository.responsesBDPost.map {
            try {
                IncomingMessage.PostsMessage(it)
            } catch (e: Exception) {
                IncomingMessage.Unknown("error ${e.message}")
            }
        }

        return merge(postFlow, peopleFlow,postDatabaseFlow,peopleDatabaseFlow)
    }
    private fun parsePost(json: JSONObject): PostTable {
        Log.d("MyTag_handler", "parsing post")

        val id = json.getInt("id")
        val date = json.optString("date")
        val time = json.getString("time")
        val copywritter = json.getJSONArray("kop").getString(0)
        val designer = json.getJSONArray("dis").getString(0)
        val theme = json.optString("theme")
        val addInfo = json.optString("add_inf")
        val tags = json.getJSONArray("tegs").getString(0)
        val textStatusString = json.optString("text_status")
        Log.d("MyTag_handler","textstatus $textStatusString")
        val textStatus = STATUS.getCodeByValue(textStatusString)
        val pict = json.optString("pict")
        val pictStatus = STATUS.getCodeByValue(json.optString("pict_status"))

        return PostTable(
            id = id,
            date = date,
            time = time,
            copywritter = copywritter,
            designer = designer,
            theme = theme,
            add_info = addInfo,
            tags = tags,
            text_status = textStatus!!,
            pict = pict,
            pict_status = pictStatus!!
        )

    }
    private fun parsePeople(json: JSONObject): PeopleTable {
        Log.d("MyTag_handler", "parsing people")

        val name = json.optString("Name")
        val role = json.optString("Role")
        val roleCode = ROLE.getCodeByValue(role)
//        val right = json.getInt("Right")
        return PeopleTable(
            name = name,
            role = roleCode!!,
            right = null,
            texrColor = null,
            backColor = null,
            workStatus = 0
        )
    }

    fun disconnect() {
        postRepository.disconnect()
    }
}

