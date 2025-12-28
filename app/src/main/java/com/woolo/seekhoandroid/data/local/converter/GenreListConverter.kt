package com.woolo.seekhoandroid.data.local.converter

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class GenreListConverter {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.let { adapter.fromJson(it) }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.let { adapter.toJson(it) }
    }
}

