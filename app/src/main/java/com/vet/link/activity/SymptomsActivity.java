package com.vet.link.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vet.link.R;
import com.victor.loading.rotate.RotateLoading;

public class SymptomsActivity extends AppCompatActivity {

    String link;
    WebView myWebView;
    RotateLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        if (getIntent() != null){
            link = getIntent().getStringExtra("link");
//            links = link.split("/ ,");
//            Log.d("the_", "onCreate: "+links[0]);
        }





        myWebView = (WebView) findViewById(R.id.webview);
        loading = findViewById(R.id.rotateloading);
        loading.start();

        setMyWebView();
    }


    @SuppressLint("SetJavaScriptEnabled")
    void setMyWebView() {
        myWebView.loadUrl(link);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebViewClient(new WebViewClient());
        if (Build.VERSION.SDK_INT >= 19) {
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress >= 90) {
                    loading.stop();
                }
            }
        });


    }
}
