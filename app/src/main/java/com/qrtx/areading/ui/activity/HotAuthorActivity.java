package com.qrtx.areading.ui.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.HotAuthor;
import com.qrtx.areading.utils.LogUtil;

public class HotAuthorActivity extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_author);
        setTitle(R.string.bookmark);
        initView();

    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.id_pb_author);
        initWebView();
    }

    private void initWebView() {

        mWebView = (WebView) findViewById(R.id.id_wv_author);
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        HotAuthor hotAuthor = (HotAuthor) getIntent().getSerializableExtra(Constants.KEY_HOTAUTHOR);
        final String path = hotAuthor.getPersionPagerUrl();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(path);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.i("<------onPageStarted------>");
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i("<------onPageFinished------>");
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtil.i("<------onReceivedError------>");
                mProgressBar.setVisibility(View.GONE);
            }
        });


        mWebView.loadUrl(path);
    }
}
