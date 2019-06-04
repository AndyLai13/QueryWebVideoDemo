package com.andylai.querywebvideodemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("Registered")
public class MainWithJava extends AppCompatActivity {
    WebView mWebView;

    private final String JS_defineFunctionPlaying = "Object.defineProperty(HTMLMediaElement.prototype, 'playing', {" +
            "    get: function() {" +
            "      return !!(this.currentTime > 0" +
            "          && !this.paused" +
            "          && !this.ended" +
            "          && this.readyState > 2);" +
            "    }" +
            "})";

    private final String JS_isVideoPlayingJSInterface = "(function() {" +
            "   var matches = document.querySelectorAll('video');" +
            "   for (i = 0; i < matches.length; i++) {" +
            "       if (matches[i].playing) {" +
            "           window.HtmlViewer.showHtml(true);" +
            "           return; " +
            "       }" +
            "   }" +
            "   window.HtmlViewer.showHtml(false);" +
            "   return; " +
            "})();";

    private final String JS_isVideoPlayingValueCallback ="(function() {" +
            "   var matches = document.querySelectorAll('video');" +
            "   for (i = 0; i < matches.length; i++) {" +
            "       if (matches[i].playing) {" +
            "           return true; " +
            "       }" +
            "   }" +
            "   return false; " +
            "})();";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://www.youtube.com/watch?v=QhLpZ8O9wyE");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("Andy", "onPageFinished");
                mWebView.loadUrl("javascript:" + JS_defineFunctionPlaying);
            }
        });

        findViewById(R.id.buttonValueCallback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.evaluateJavascript("javascript:"+ JS_isVideoPlayingValueCallback,
                        new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.d("Andy", "JS_isVideoPlayingValueCallback = " + value);
                    }
                });
            }
        });

        findViewById(R.id.buttonJSInterface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:" + JS_isVideoPlayingJSInterface);
            }
        });
    }

    @SuppressWarnings("unused")
    class MyJavaScriptInterface {

        @JavascriptInterface
        public void showHtml(boolean html) {
            Log.d("Andy", "JS_isVideoPlayingJSInterface = " + html);
        }
    }
}
