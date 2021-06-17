package me.inassar.webviewinteractionspoc.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.google.gson.Gson
import me.inassar.webviewinteractionspoc.data.ParamOptions
import me.inassar.webviewinteractionspoc.data.Payload
import me.inassar.webviewinteractionspoc.data.PayloadDeserializer
import java.io.ByteArrayOutputStream

object Utils {

    fun getPayloadObject(jsonPayload: String): Payload {
        return Gson().newBuilder().registerTypeAdapter(Payload::class.java, PayloadDeserializer())
            .create().fromJson(jsonPayload, Payload::class.java)
    }

    fun getParams(param: Any?): Any {
        return when (param) {
            is ParamOptions -> {
                ParamOptions(param.op1, param.op2)
            }
            else -> {
                param.toString()
            }
        }
    }

    fun toBase64(fileUri: Uri?, context: Context): String {
        // Convert uri to bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, fileUri)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        // Encode bitmap and convert it to base64
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}