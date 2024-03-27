package com.aspirant.weeklytasktrackerapp.model.entity.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

sealed class ApiResponse<out T> {
    data class Success<T>(val success: Boolean, val value: T) : ApiResponse<T>()
    data class Failure(val success: Boolean, val error: String) : ApiResponse<Nothing>()

    companion object {
        inline fun <reified T> typeToken(): Type {
            return object : TypeToken<ApiResponse<T>>() {}.type
        }

        fun <T> success(value: T): ApiResponse<T> = Success(true, value)
        fun failure(error: String): ApiResponse<Nothing> = Failure(false, error)
    }
}

class ApiResponseAdapter : JsonSerializer<ApiResponse<*>>, JsonDeserializer<ApiResponse<*>> {
    override fun serialize(
        src: ApiResponse<*>?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        when (src) {
            is ApiResponse.Success -> {
                jsonObject.addProperty("success", src.success)
                jsonObject.add("value", context?.serialize(src.value))
            }

            is ApiResponse.Failure -> {
                jsonObject.addProperty("success", src.success)
                jsonObject.addProperty("error", src.error)
            }

            null -> {
                jsonObject.addProperty("success", false)
                jsonObject.addProperty("error", "error in serialize")
            }
        }
        return jsonObject
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ApiResponse<*> {
        val jsonObject = json?.asJsonObject
        return when {
            jsonObject?.has("value") == true -> {
                val valueJsonElement = jsonObject.get("value")
                val value = if (valueJsonElement.isJsonPrimitive) {
                    // Handle the case when the value is a primitive
                    when {
                        valueJsonElement.asJsonPrimitive.isString -> {
                            // If the value is a JSON string, return it directly
                            valueJsonElement.asString
                        }

                        valueJsonElement.asJsonPrimitive.isBoolean -> {
                            // If the value is a JSON boolean, return it directly
                            valueJsonElement.asBoolean
                        }

                        valueJsonElement.asJsonPrimitive.isNumber -> {
                            // If the value is a JSON number, return it directly
                            valueJsonElement.asNumber
                        }

                        else -> {
                            ApiResponse.failure("some unknown primitive was passed")
                        }
                    }
                } else {
                    context?.deserialize<Any>(valueJsonElement, typeOfT)
                }
                ApiResponse.success(value)
            }

            jsonObject?.has("error") == true -> {
                val error = jsonObject.get("error").asString
                ApiResponse.failure(error)
            }

            else -> {
                ApiResponse.failure("Unknown error")
            }
        }
    }

}
