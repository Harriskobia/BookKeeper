package com.room.ps

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "books")
class Book(@PrimaryKey
           @NonNull
           val id: String,

           val author: String?,

           val book: String?,

           val description: String?,

           @ColumnInfo(name = "last_updated")
           val lastUpdated: Date?)