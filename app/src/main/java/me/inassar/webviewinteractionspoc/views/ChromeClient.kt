package me.inassar.webviewinteractionspoc.views

import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebView

class ChromeClient(private val viewModel: MainActivityViewModel) : WebChromeClient() {
    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        result!!.cancel()

        // Do what ever you want here with payload data
        viewModel.getPayload(message!!)

        return true
    }
}