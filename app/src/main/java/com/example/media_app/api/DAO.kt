package com.example.media_app.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun Insert(post: PostTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun Insert(post: PeopleTable)

    @Update
    suspend fun Update(post: PostTable)

    @Update
    suspend fun Update(post: PeopleTable)

    @Delete
    suspend fun Delete(post: PostTable)

    @Delete
    suspend fun Delete(post: PeopleTable)

    @Query("SELECT * FROM post WHERE id = :idPost")
    fun getPost(idPost:Int) :LiveData<PostTable>
    @Query("SELECT * FROM post")
    fun getAllPost() : Flow<List<PostTable>>

    @Query("DELETE FROM post")
    suspend fun deleteAllPost()

    @Query("DELETE FROM post WHERE id =:idPost")
    suspend fun deletePost(idPost:Int)

    @Query("SELECT * FROM people")
    fun getAllPeople() : Flow<List<PeopleTable>>

    @Transaction
    suspend fun insertPostAndUpdatePeople(post: PostTable) {
        // Вставляем новый пост
        Insert(post)

        // Обновляем статус у дизайнера
        UpdatePeopleWorkStatus(post.designer)
        Log.d("MyTag_DAO","set working ${post.designer}")

        // Обновляем статус у копирайтера
        UpdatePeopleWorkStatus(post.copywritter)
        Log.d("MyTag_DAO","set working ${post.copywritter}")
    }

    // Обновляем workStatus у человека (дизайнера или копирайтера)
    @Query("UPDATE people SET workStatus = 1 WHERE name = :name")
    suspend fun UpdatePeopleWorkStatus(name: String)

    // По возрастанию (switchState == 0)
    @Query(" SELECT name, role, COUNT(post.id) AS post_count FROM people LEFT JOIN post ON people.name = post.designer OR people.name = post.copywritter GROUP BY name ORDER BY post_count ASC")
    suspend fun getAllPeoplePostCountsAsc(): List<PeoplePostCount>

    // Без фильтра, по убыванию
    @Query(" SELECT name, role, COUNT(post.id) AS post_count FROM people LEFT JOIN post ON people.name = post.designer OR people.name = post.copywritter GROUP BY name ORDER BY post_count DESC")
    suspend fun getAllPeoplePostCountsDesc(): List<PeoplePostCount>

    // С фильтром по роли, по возрастанию
    @Query(" SELECT name, role, COUNT(post.id) AS post_count FROM people LEFT JOIN post ON people.name = post.designer OR people.name = post.copywritter WHERE role = :role GROUP BY name ORDER BY post_count ASC")
    suspend fun getPeoplePostCountsByRoleAsc(role: Int): List<PeoplePostCount>

    // С фильтром по роли, по убыванию
    @Query(" SELECT name, role, COUNT(post.id) AS post_count FROM people LEFT JOIN post ON people.name = post.designer OR people.name = post.copywritter WHERE role = :role GROUP BY name ORDER BY post_count DESC")
    suspend fun getPeoplePostCountsByRoleDesc(role: Int): List<PeoplePostCount>

    // 👇 Универсальная функция, вызываемая из ViewModel или репозитория
    suspend fun getPeoplePostCounts(switchState: Int, spinnerValue: Int): List<PeoplePostCount> {
        return when {
            spinnerValue == 0 && switchState == 0 -> getAllPeoplePostCountsAsc()
            spinnerValue == 0 && switchState == 1 -> getAllPeoplePostCountsDesc()
            switchState == 1 -> getPeoplePostCountsByRoleAsc(spinnerValue)
            else -> getPeoplePostCountsByRoleDesc(spinnerValue)
        }
    }





}
