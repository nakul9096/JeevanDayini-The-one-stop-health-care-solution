package com.example.unityapp;
// Privacy.java

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Privacy extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        // Find the WebView and ProgressBar by their IDs
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        // Set initial visibility to VISIBLE
        progressBar.setVisibility(View.VISIBLE);

        // Set a WebViewClient to handle URL loading within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Page loading started, show ProgressBar
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Page loading finished, hide ProgressBar
                progressBar.setVisibility(View.GONE);
            }
        });

        // Set a WebChromeClient to handle the progress bar updates
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Update the progress bar based on the loading progress
                progressBar.setProgress(newProgress);
            }
        });

        // Enable JavaScript (if needed)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load a URL (replace "https://www.example.com" with your desired URL)
        webView.loadUrl("https://jeevandayini.blogspot.com/2024/01/privacy-policy.html");
    }
}
