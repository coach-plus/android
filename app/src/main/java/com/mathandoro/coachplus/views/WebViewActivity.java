package com.mathandoro.coachplus.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

public class WebViewActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {
    public static final String URL = "url";
    private ToolbarFragment toolbarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url  = getIntent().getStringExtra(URL);
        if (url == null || url.isEmpty()) finish();

        setContentView(R.layout.webview_activity);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.webview_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle("");

        toolbarFragment.showBackButton();

        WebView webView = findViewById(R.id.webview_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {

    }
}