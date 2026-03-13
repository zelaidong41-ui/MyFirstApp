package com.example.myfirstapp; // 注意：如果你的包名不一样，保留你原来最上面那行 package 开头的代码

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 绑定咱们刚才确认过的纯净版 XML 皮肤
        setContentView(R.layout.activity_detail);

        // 2. 找到 WebView 并给它通电
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new android.webkit.WebViewClient());

        // 3. 接收 Adapter 传过来的搜索网址，并加载它！
        String url = getIntent().getStringExtra("ARTICLE_URL");
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}