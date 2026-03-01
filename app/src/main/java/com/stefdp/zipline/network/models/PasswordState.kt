package com.stefdp.zipline.network.models

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

sealed class PasswordState {
    data class Text(val value: String) : PasswordState()
    data class Flag(val value: Boolean) : PasswordState()
}

val PasswordState.value
    get() = when (this) {
        is PasswordState.Text -> value
        is PasswordState.Flag -> value
    }

class PasswordStateDeserializer : JsonDeserializer<PasswordState> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PasswordState? {
        if (json.isJsonNull) return null

        val primitive = json.asJsonPrimitive
        return when {
            primitive.isBoolean -> PasswordState.Flag(primitive.asBoolean)
            primitive.isString -> PasswordState.Text(primitive.asString)
            else -> null // Or throw JsonParseException("Unexpected type")
        }
    }
}

val passwordStateConverterFactory = GsonBuilder()
    .registerTypeAdapter(PasswordState::class.java, PasswordStateDeserializer())
    .create()