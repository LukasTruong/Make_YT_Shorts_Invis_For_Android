package com.hakim.noshorts

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import com.hakim.noshorts.R
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)

        ViewCompat.setOnApplyWindowInsetsListener(webView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.path?.startsWith("/shorts") == true) {
                    view.loadUrl("https://www.youtube.com/")
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectCSS(view)
                injectJS(view)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                customView = view
                customViewCallback = callback
                (window.decorView as FrameLayout).addView(
                    view, FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
                webView.visibility = View.GONE
                webView.setPadding(0,0,0,0)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }

            override fun onHideCustomView() {
                (window.decorView as FrameLayout).removeView(customView)
                customView = null
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                webView.visibility = View.VISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                ViewCompat.requestApplyInsets(webView)
            }
        }

        webView.loadUrl("https://www.youtube.com/")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
                else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

    }

    private fun injectCSS(view: WebView) {
        val css = assets.open("styles.css")
            .bufferedReader().use { it.readText() }
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")

        view.evaluateJavascript("""
            (function() {
                var existing = document.getElementById('no-shorts-css');
                if (existing) return;
                var style = document.createElement('style');
                style.id = 'no-shorts-css';
                style.textContent = '$css';
                document.head.appendChild(style);
            })();
        """.trimIndent(), null)
    }

    private fun injectJS(view: WebView) {
        val js = assets.open("content.js").bufferedReader().use { it.readText() }
        view.evaluateJavascript(js, null)
    }


}