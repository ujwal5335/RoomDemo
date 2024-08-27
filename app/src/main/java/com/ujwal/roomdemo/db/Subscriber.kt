package com.ujwal.roomdemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// If we don't give any table name, then room automatically picks the data class name as table name.
@Entity(tableName = "subscriber_table_name")
data class Subscriber(
    // Give column name for best practice.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subscriber_id")
    val id: Int,

    @ColumnInfo(name = "subscriber_name")
    var name: String,

    @ColumnInfo(name = "subscriber_email")
    var email: String
)