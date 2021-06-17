package me.inassar.webviewinteractionspoc.extensions

import androidx.appcompat.app.AppCompatActivity
import com.tapadoo.alerter.Alerter
import me.inassar.webviewinteractionspoc.R

fun AppCompatActivity.successAlert(
    message: String, duration: Long = 5000, color: Int = android.R.color.holo_green_light
): Alerter {
    return Alerter.create(this).apply {
        setTitle("Success")
        setText(message)
        setBackgroundColorRes(color)
        setDuration(duration)
        enableSwipeToDismiss()
        show()
    }
}

fun AppCompatActivity.warningAlert(
    message: String, duration: Long = 5000,
    enableBtns: Boolean = false,
    positiveBtnTitle: String = "Okay",
    negativeBtnTitle: String = "No",
    positiveBtnClick: () -> Unit,
    negativeBtnClick: () -> Unit
): Alerter {
    return Alerter.create(this).apply {
        setTitle("Alert")
        setText(message)
        setBackgroundColorRes(android.R.color.holo_orange_light)
        setDuration(duration)
        enableSwipeToDismiss()
        if (enableBtns) {
            addButton(negativeBtnTitle, R.style.AlertButton) {
                negativeBtnClick()
                Alerter.hide()
            }
            addButton(positiveBtnTitle, R.style.AlertButton) {
                positiveBtnClick()
                Alerter.hide()
            }
        }
        show()
    }
}