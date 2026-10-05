package com.mwplay.app

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(PlayerBridge(), "MWPlayNative")

        // Hosted MW Play UI. This remains source-agnostic; the native bridge only
        // receives direct media URLs the web client has already resolved.
        webView.loadUrl("https://m-wplay.vercel.app")
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    inner class PlayerBridge {
        @JavascriptInterface
        fun play(url: String, title: String?) {
            if (!url.startsWith("https://") && !url.startsWith("http://")) return
            startActivity(Intent(this@MainActivity, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.EXTRA_URL, url)
                putExtra(PlayerActivity.EXTRA_TITLE, title ?: "MW Play")
            })
        }
    }
}
