package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article); // 咱们的精装图纸

        // 1. 找到咱们刚摆进去的三件家具
        WebView myWebView = findViewById(R.id.myWebView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tvBack = findViewById(R.id.tvBack);

        // 2. 赋予“返回按钮”超能力：退回大厅！
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 3. 基础配置
        myWebView.getSettings().setJavaScriptEnabled(true);
        // ==========================================
        // 🛡️ 给 WebView 装上大厂级防弹保安！
        // ==========================================
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest request) {
                String url = request.getUrl().toString();

                // 1. 如果是正常的网址，保安放行，让 WebView 自己乖乖加载
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }

                // 2. 如果遇到“唤醒App”的火星文协议（比如 juejin://, taobao://）
                try {
                    // 尝试去呼叫手机里的真实 App（比如打开真正的掘金、淘宝）
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    // 3. 如果手机里没装那个 App，那就当作无事发生，绝不让 WebView 崩溃白屏！
                    android.widget.Toast.makeText(ArticleActivity.this, "未安装对应App，无法跳转", android.widget.Toast.LENGTH_SHORT).show();
                }

                // 告诉 WebView：这个神秘火星链接我已经亲自处理了，你不用管了，继续保持原样！
                return true;
            }
        });

        // 4. 装上 Chrome 监控探头，实时接管进度条！
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

        // 5. 接客，加载网页！
        String targetUrl = getIntent().getStringExtra("URL_KEY");
        if (targetUrl != null) {
            myWebView.loadUrl(targetUrl);
        }
    }
}