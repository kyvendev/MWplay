package com.mwplay.app

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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
        webView.addJavascriptInterface(PlayerBridge(), "MWPlayNative")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                installNativePlayerHook(view)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }
        }

        // Hosted MW Play preview currently used for Android testing.
        // Media3 handles direct HTTP(S) media streams exposed by the web player.
        webView.loadUrl("https://m-wplay-n8r2az57r-kyvendev.vercel.app/#/")
    }

    private fun installNativePlayerHook(view: WebView) {
        val script = """
            (() => {
              if (window.__mwNativePlayerHookInstalled) return;
              window.__mwNativePlayerHookInstalled = true;
              const sent = new Set();
              const forward = (media) => {
                try {
                  const src = media.currentSrc || media.src || media.querySelector?.('source')?.src;
                  if (!src || !/^https?:\/\//i.test(src) || sent.has(src)) return;
                  sent.add(src);
                  const title = document.querySelector('h1,h2')?.textContent || document.title || 'MW Play';
                  window.MWPlayNative?.play(src, title);
                } catch (_) {}
              };
              const scan = () => document.querySelectorAll('video,audio').forEach((media) => {
                media.addEventListener('error', () => forward(media), { passive: true });
                media.addEventListener('loadedmetadata', () => {
                  if (media.dataset.mwNativeRequested === '1') forward(media);
                }, { passive: true });
              });
              scan();
              new MutationObserver(scan).observe(document.documentElement, { childList: true, subtree: true });
              window.MWPlay = window.MWPlay || {};
              window.MWPlay.openNativePlayer = (url, title) => window.MWPlayNative?.play(url, title || document.title || 'MW Play');
            })();
        """.trimIndent()
        view.evaluateJavascript(script, null)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    inner class PlayerBridge {
        @JavascriptInterface
        fun play(url: String, title: String?) {
            if (!url.startsWith("https://") && !url.startsWith("http://")) return
            runOnUiThread {
                startActivity(Intent(this@MainActivity, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_URL, url)
                    putExtra(PlayerActivity.EXTRA_TITLE, title ?: "MW Play")
                })
            }
        }
    }
}
