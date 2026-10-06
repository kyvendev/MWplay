package com.mwplay.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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

        webView.loadUrl("https://m-wplay.vercel.app/#/")
    }

    private fun installNativePlayerHook(view: WebView) {
        val script = """
            (() => {
              if (window.__mwNativePlayerHookInstalled) return;
              window.__mwNativePlayerHookInstalled = true;
              const sent = new Set();
              const isLocalStreamingServer = (value) => {
                try {
                  const u = new URL(value, location.href);
                  return u.hostname === '127.0.0.1' || u.hostname === 'localhost' || u.hostname === '::1';
                } catch (_) { return false; }
              };
              const forward = (media) => {
                try {
                  const src = media.currentSrc || media.src || media.querySelector?.('source')?.src;
                  if (!src || !/^https?:\/\//i.test(src) || sent.has(src)) return;
                  sent.add(src);
                  const title = document.querySelector('h1,h2')?.textContent || document.title || 'MW Play';
                  if (isLocalStreamingServer(src)) {
                    window.MWPlayNative?.unsupportedLocalStream(src, title);
                    return;
                  }
                  window.MWPlayNative?.play(src, title);
                } catch (_) {}
              };
              const scan = () => document.querySelectorAll('video,audio').forEach((media) => {
                if (media.dataset.mwHooked === '1') return;
                media.dataset.mwHooked = '1';
                media.addEventListener('error', () => forward(media), { passive: true });
              });
              scan();
              new MutationObserver(scan).observe(document.documentElement, { childList: true, subtree: true });
              window.MWPlay = window.MWPlay || {};
              window.MWPlay.openNativePlayer = (url, title) => {
                if (isLocalStreamingServer(url)) {
                  window.MWPlayNative?.unsupportedLocalStream(url, title || document.title || 'MW Play');
                  return false;
                }
                window.MWPlayNative?.play(url, title || document.title || 'MW Play');
                return true;
              };
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
            val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
            val scheme = uri.scheme?.lowercase()
            if (scheme != "https" && scheme != "http") return
            if (isLoopback(uri.host)) {
                unsupportedLocalStream(url, title)
                return
            }
            runOnUiThread {
                startActivity(Intent(this@MainActivity, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_URL, url)
                    putExtra(PlayerActivity.EXTRA_TITLE, title ?: "MW Play")
                })
            }
        }

        @JavascriptInterface
        fun unsupportedLocalStream(url: String, title: String?) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Esta fonte depende de um servidor de streaming local e não pode ser aberta diretamente no player Android. Escolha uma fonte HTTP(S)/HLS/DASH direta.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isLoopback(host: String?): Boolean {
        val value = host?.trim('[', ']')?.lowercase() ?: return false
        return value == "127.0.0.1" || value == "localhost" || value == "::1"
    }
}
