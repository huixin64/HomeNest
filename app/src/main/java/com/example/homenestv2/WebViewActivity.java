package com.example.homenestv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Title will be set based on the content loaded
        }

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // Keep links within the app

        // Get URL and title from intent extras
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        if (title != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            // Handle error, maybe show an error message
            webView.loadData("<html><body><h1>Error</h1><p>Could not load content.</p></body></html>", "text/html", "UTF-8");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Simply finish the activity to return to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 