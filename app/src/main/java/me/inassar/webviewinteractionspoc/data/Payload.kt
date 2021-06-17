package me.inassar.webviewinteractionspoc.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

data class Payload(
    var type: String? = null,
    var param: Any? = null
)

data class ParamOptions(
    val op1: String,
    val op2: String
)

class PayloadDeserializer : JsonDeserializer<Payload> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Payload {
        json as JsonObject

        val type = json.get("type").asString
        val paramJson = json.get("param")
        val param =
            when {
                paramJson.isJsonObject -> {
                    val op1 = paramJson.asJsonObject.get("op1").asString
                    val op2 = paramJson.asJsonObject.get("op2").asString
                    ParamOptions(op1, op2)
                }
                paramJson.isJsonNull -> ""
                else -> paramJson.asString
            }
        return Payload(type, param)
    }
}