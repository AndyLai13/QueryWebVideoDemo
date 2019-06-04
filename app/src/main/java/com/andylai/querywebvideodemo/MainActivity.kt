@file:Suppress("RedundantSamConstructor")

package com.andylai.querywebvideodemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

/**
 * About JavaScript injection:
 *
 *    After any url has been loaded, then we want inject JS code after url,
 *    "javascript:" is applied to put before any content.
 *
 *    only inject code: webView.load("javascript:{JS_code}")
 *    get return value: webView.evaluateJavascript("javascript:{JS_code}", ValueCallback(){...})
 *
 *
 * There are two ways to retrieve value from JavaScript back to android,
 *
 * 1. ValueCallback: get the return value after any function has been executed.
 *
 * 2. JavaScriptInterface: define an Interface, then call addJavascriptInterface(Object, String) with
 *    first parm is JavascriptInterface and second one is name of this interface.
 *    Name is the key for communication between Android and JS, simply use
 *    "window.{Name}.{self-defined function}" to get return value and take it from function defined in
 *    JavascriptInterface.
 *
 */
@Suppress("PropertyName", "PrivatePropertyName")
class MainActivity : AppCompatActivity() {

    private val JS_defineFunctionPlaying = "Object.defineProperty(HTMLMediaElement.prototype, 'playing', {" +
            "    get: function() {" +
            "      return !!(this.currentTime > 0" +
            "          && !this.paused" +
            "          && !this.ended" +
            "          && this.readyState > 2);" +
            "    }" +
            "})"

    private val JS_isVideoPlayingJSInterface = "(function() {" +
            "   var matches = document.querySelectorAll('video');" +
            "   for (i = 0; i < matches.length; i++) {" +
            "       if (matches[i].playing) {" +
            "           window.HtmlViewer.showHtml(true);" +
            "           return; " +
            "       }" +
            "   }" +
            "   window.HtmlViewer.showHtml(false);" +
            "   return; " +
            "})();"

    private val JS_isVideoPlayingValueCallback ="(function() {" +
            "   var matches = document.querySelectorAll('video');" +
            "   for (i = 0; i < matches.length; i++) {" +
            "       if (matches[i].playing) {" +
            "           return true; " +
            "       }" +
            "   }" +
            "   return false; " +
            "})();"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("https://www.youtube.com/watch?v=QhLpZ8O9wyE")
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HtmlViewer")

        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("Andy", "onPageFinished")
                webView.loadUrl("javascript:$JS_defineFunctionPlaying")
            }
        }

        findViewById<Button>(R.id.buttonValueCallback).setOnClickListener(View.OnClickListener {
            webView.evaluateJavascript("javascript:$JS_isVideoPlayingValueCallback", ValueCallback {
                Log.d("Andy", "JS_isVideoPlayingValueCallback =  $it")
            })
        })

        findViewById<Button>(R.id.buttonJSInterface).setOnClickListener(View.OnClickListener {
            webView.loadUrl("javascript:$JS_isVideoPlayingJSInterface")
        })
    }

    inner class MyJavaScriptInterface {

        @JavascriptInterface
        fun showHtml(html: Boolean) {
            Log.d("Andy", "JS_isVideoPlayingJSInterface =  $html")
        }
    }
}
