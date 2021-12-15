package com.example.browserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageView imageView;
    WebView webView;
    LinearLayout linearLayout;
    //SwipeRefreshLayout swipeRefreshLayout;
    String myCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.myLinearLayout);
        progressBar = findViewById(R.id.myProgressBar);
        imageView= findViewById(R.id.myImageView);
        webView = findViewById(R.id.myWebView);

        progressBar.setMax(100);

        webView.loadUrl("https://duckduckgo.co");
        //webView.loadUrl("https://chrome.google.com");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                linearLayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
                myCurrentUrl = url;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });


        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                imageView.setImageBitmap(icon);
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String smimetype, long contentLength) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this, "Your file is downloading...",Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Your file has downloaded",Toast.LENGTH_SHORT).show();
            }
        });


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                webView.reload();
//            }
//        });
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_back:
                onBackPressed();
                break;

            case R.id.menu_forward:
                onForwardPressed();
                break;

            case R.id.menu_refresh:
                webView.reload();
                break;

            case R.id.menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, myCurrentUrl);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Copied URL");
                startActivity(Intent.createChooser(shareIntent, "Share URL with friends"));
        }
        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed(){
        if(webView.canGoForward()){
            webView.goForward();
        }else {
            Toast.makeText(this, "can't go further!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
