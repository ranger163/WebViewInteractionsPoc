package me.inassar.webviewinteractionspoc.views

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import me.inassar.webviewinteractionspoc.app.AppConstants
import me.inassar.webviewinteractionspoc.app.AppConstants.ALERT
import me.inassar.webviewinteractionspoc.app.AppConstants.ALERT_OPTIONS
import me.inassar.webviewinteractionspoc.app.AppConstants.LOCATION
import me.inassar.webviewinteractionspoc.app.AppConstants.NOTIFICATION
import me.inassar.webviewinteractionspoc.app.AppConstants.TOKEN
import me.inassar.webviewinteractionspoc.app.AppConstants.UPLOAD
import me.inassar.webviewinteractionspoc.data.ParamOptions
import me.inassar.webviewinteractionspoc.extensions.successAlert
import me.inassar.webviewinteractionspoc.extensions.warningAlert
import me.inassar.webviewinteractionspoc.utils.Utils.toBase64
import me.inassar.webviewinteractionspoc.utils.createNotificationChannel
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWebView()
        setContentView(webView)

        interactions()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView = WebView(this).apply {
            // Load URL from web
            loadUrl(AppConstants.URL_LOCAL)
            // Stop user from copy, past or highlighting text from webview
            setOnLongClickListener { true }
            isLongClickable = false
            // Enable javascript in webView
            settings.javaScriptEnabled = true
            // Gain access to webView JS prompts
            webChromeClient = ChromeClient(viewModel)
        }
    }

    private fun interactions() {
        viewModel.apply {

            getJwtTokenClickEvent.observe(this@MainActivity) { message ->
                successAlert(message)
            }

            alertMeClickEvent.observe(this@MainActivity) { enteredText ->
                successAlert("You have entered this text: $enteredText")
            }

            giveMeOptionsClickEvent.observe(this@MainActivity) { param ->
                warningAlert(
                    message = "Tap on your favourite color",
                    enableBtns = true,
                    positiveBtnTitle = param.op1,
                    negativeBtnTitle = param.op2,
                    positiveBtnClick = {
                        successAlert(
                            message = "You have changed color to blue",
                            color = android.R.color.holo_blue_light
                        )
                    },
                    negativeBtnClick = {
                        successAlert(
                            message = "You have changed color to red",
                            color = android.R.color.holo_red_light
                        )
                    }
                )
            }

            getLocationClickEvent.observe(this@MainActivity) { location ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(location)
                    ).setPackage("com.google.android.apps.maps")
                )
            }

            getNotificationClickEvent.observe(this@MainActivity) {
                createNotificationChannel()
            }

            uploadImageClickEvent.observe(this@MainActivity) {
                initImagePicker()
            }

            getPayload.observe(this@MainActivity) { payload ->
                defineActions(payload.type!!, payload.param!!)
            }
        }
    }

    /**
     * This is used to define which type of actions we receive from the webView
     * @param type
     * @param param
     */
    private fun defineActions(type: String, param: Any) {
        when (type) {
            TOKEN -> {
                viewModel.getJwtToken("Your token is\n\neyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            }
            ALERT -> {
                // extract value from text input
                webView!!.evaluateJavascript(
                    "(function() { return (document.getElementById(\"inputForAlert\").value); })();"
                ) { text ->
                    viewModel.alertMe(text)
                }
            }
            ALERT_OPTIONS -> {
                viewModel.giveMeOptions(param as ParamOptions)
            }
            LOCATION -> {
                viewModel.getLocation("http://maps.google.com/maps?q=loc:24.7829419,46.6423292")
            }
            NOTIFICATION -> {
                viewModel.getNotification(application)
            }
            UPLOAD -> {
                viewModel.uploadImage()
            }
        }
    }

    private fun initImagePicker() {
        ImagePicker.with(this)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultCodeHandling(resultCode, data!!)
    }

    private fun resultCodeHandling(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data.data

                val encodedImage = toBase64(fileUri, this@MainActivity)

                // display image to it's html element
                val retFunction =
                    "document.getElementById(\"imgElem\").setAttribute('src','data:image/jpeg;base64," + URLEncoder.encode(
                        encodedImage,
                        "UTF-8"
                    ).toString() + "');"
                webView!!.evaluateJavascript(retFunction, null)
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}