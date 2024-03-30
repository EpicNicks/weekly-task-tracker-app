package com.aspirant.weeklytasktrackerapp.model.entity.response

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType

class ListTypeAdapter<E>(private val adapter: TypeAdapter<E>) : TypeAdapter<List<E>>() {

    companion object {
        val FACTORY: TypeAdapterFactory = object : TypeAdapterFactory {
            override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
                val rawType = type.rawType as? Class<T>
                if (rawType != List::class.java) {
                    return null
                }
                val parameterizedType = type.type as? ParameterizedType ?: return null
                val actualType = parameterizedType.actualTypeArguments[0]
                val adapter = gson.getAdapter(TypeToken.get(actualType))
                @Suppress("UNCHECKED_CAST")
                return ListTypeAdapter(adapter) as TypeAdapter<T>
            }
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): List<E> {
        `in`.beginArray()
        val ls = mutableListOf<E>()
        while (`in`.peek() != JsonToken.END_ARRAY) {
            ls.add(adapter.read(`in`))
        }
        `in`.endArray()
        return ls.toList()
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: List<E>) {
        out.beginArray()
        for (e in value) {
            adapter.write(out, e)
        }
        out.endArray()
    }
}