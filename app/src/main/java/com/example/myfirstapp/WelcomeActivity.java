package com.example.myfirstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private EditText etWeight;
    private Button btnBoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 因为“查户口”的工作交给了 DailyCheckInActivity，这里不用再写判断逻辑了，直接显示界面！
        setContentView(R.layout.activity_welcome);

        etWeight = findViewById(R.id.etWeight);
        btnBoot = findViewById(R.id.btnBoot);

        // 💾 点击按钮，只保存静态体重，然后去“每日打卡页”
        btnBoot.setOnClickListener(v -> {
            String weightStr = etWeight.getText().toString();

            if (weightStr.isEmpty()) {
                Toast.makeText(this, "Sir，必须输入体重才能初始化引擎！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. 把体重永久存进本地记忆
            float weight = Float.parseFloat(weightStr);
            SharedPreferences prefs = getSharedPreferences("JarvisPrefs", MODE_PRIVATE);
            prefs.edit().putFloat("USER_WEIGHT", weight).apply();

            Toast.makeText(this, "基础档案建立完毕！进入今日调度...", Toast.LENGTH_SHORT).show();

            // 🚀 2. 核心改变：带着填好的档案，跳去“每日打卡”问今天的强度！
            startActivity(new Intent(this, DailyCheckInActivity.class));
            finish();
        });
    }
}