package me.inassar.webviewinteractionspoc.views

import android.app.Application
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.inassar.webviewinteractionspoc.R
import me.inassar.webviewinteractionspoc.app.SingleLiveEvent
import me.inassar.webviewinteractionspoc.data.ParamOptions
import me.inassar.webviewinteractionspoc.data.Payload
import me.inassar.webviewinteractionspoc.utils.Utils
import me.inassar.webviewinteractionspoc.utils.sendNotification

class MainActivityViewModel : ViewModel() {

    val getJwtTokenClickEvent = SingleLiveEvent<String>()
    val alertMeClickEvent = SingleLiveEvent<String>()
    val giveMeOptionsClickEvent = SingleLiveEvent<ParamOptions>()
    val getLocationClickEvent = SingleLiveEvent<String>()
    val getNotificationClickEvent = SingleLiveEvent<NotificationManager>()
    val uploadImageClickEvent = SingleLiveEvent<Void>()

    private val _getPayload = MutableLiveData<Payload>()
    val getPayload get() = _getPayload

    fun getJwtToken(message: String) {
        getJwtTokenClickEvent.value = message
    }

    fun alertMe(textInput: String) {
        alertMeClickEvent.value = textInput
    }

    fun giveMeOptions(param: ParamOptions) {
        giveMeOptionsClickEvent.value = param
    }

    fun getLocation(location: String) {
        getLocationClickEvent.value = location
    }

    fun getNotification(app: Application) {
        val notificationManager = ContextCompat.getSystemService(
            app,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(app.getString(R.string.notification_content), app)
        getNotificationClickEvent.value=notificationManager
    }

    fun uploadImage() {
        uploadImageClickEvent.call()
    }

    fun getPayload(message: String) {

        val payload = Payload()

        payload.type = Utils.getPayloadObject(message).type
        payload.param = when (val params = Utils.getParams(Utils.getPayloadObject(message).param)) {
            is ParamOptions -> {
                ParamOptions(params.op1, params.op2)
            }
            else -> {
                params
            }
        }

        _getPayload.value = payload
    }

}