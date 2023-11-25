package com.nosh.appchi.`fun`

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class Internet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(this)
        val webSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                myWebView.loadUrl(request.url.toString())
                return true
            }
        }

        val input = intent.extras?.getString("url")!!
        val url =
            if (
                input.length > 1
            ) {
                input.drop(1)
            } else {
                "google.com"
            }
        setContentView(myWebView)
        myWebView.loadUrl("about:blank"); myWebView.loadUrl("https://www.$url/")
    }
}