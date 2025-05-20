package com.example.media_app.api

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "post",
    foreignKeys = [
        ForeignKey(
            entity = PeopleTable::class,
            parentColumns = ["name"],
            childColumns = ["designer"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = PeopleTable::class,
            parentColumns = ["name"],
            childColumns = ["copywritter"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("designer"), Index("copywritter")])
data class PostTable(
    @PrimaryKey
    var id: Int,
    var date: String?,
    var time: String?,
    var copywritter: String,
    var designer: String,
    var theme: String?,
    var add_info: String?,
    var tags: String?,
    var text_status: Int,
    var pict: String?,
    var pict_status: Int
)

@Entity(tableName = "people")
data class PeopleTable(
    @PrimaryKey
    var name: String,
    var role:Int,
    var right:Int?,
    var texrColor: Int?,
    var backColor: Int?,
    var workStatus: Int?
)


data class PeoplePostCount(
    val name: String,
    val role: Int,
    val post_count: Int
)
