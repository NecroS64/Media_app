package com.example.media_app

import android.util.Log
import androidx.lifecycle.ViewModel
import java.sql.Time
import java.util.Date

enum class STATUS(val value:Int){
    green(2),
    yellow(1),
    red(0)

}


class Post( var id:Int, var date:String?,
    var time: String, var copywritter: String?, var designer:String?,
    var theme:String?,var add_info:String?, tags:String,var text_status:Int,
        var pict: String?,var pict_status:Int, var id_sheet:Int){}




class PostViewModel : ViewModel() {
    private var posts:MutableList<Post> = mutableListOf()
    /**
     * @param newpost поста который нужно добавить.
     */
    fun newPost(newpost: Post, adapterPost: MyAdapterPost) {
        val exists = posts.any { it.id == newpost.id }
        if (!exists) {
            posts.add(newpost)
            adapterPost.addItem()
        } else {
            Log.d("PostViewModel", "Пост с id ${newpost.id} уже существует")
        }
    }

    /**
     *
     * @return список постов
     */
    fun getPosts():MutableList<Post>{
        return posts
    }
    /**
     * @param id поста который нужно удалить.
     * @return пост
     */
    fun getPost(id:Int):Post{
        return posts[id]
    }
    /**
     * @param id поста который нужно удалить.
     */
    fun deletePost(id:Int)
    {
        posts.removeAt(id)
    }
    /**
     * @param id поста который нужно заменить.
     * @param post новый пост.
     */
    fun changePost(id:Int,post: Post)
    {
        posts.set(id,post)
    }

    // TODO: Implement the ViewModel

}

class PeopleViewModel : ViewModel() {
    // TODO: Implement the ViewModel

}