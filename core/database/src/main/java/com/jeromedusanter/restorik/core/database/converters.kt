package com.jeromedusanter.restorik.core.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(separator = "|")
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        if (data.isEmpty()) return emptyList()
        return data.split("|")
    }
}
