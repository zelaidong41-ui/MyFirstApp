package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_welcome);//确保是前天画的欢迎界面
        //1 找到画板上显示大字的TextView（假设他id是tvwelcome）
        TextView myWelcomeText = findViewById(R.id.tvWelcomeMessage);
        //2 检查传送门 ，拆快递，找标签为“MY_OFFER”的包裹
        Intent portal = getIntent();
        String finalOffer = portal.getStringExtra("MY_OFFER");

        //3 把横幅拉满
        if (finalOffer != null){
            myWelcomeText.setText("恭喜拿下：\n" + finalOffer + "\n\n收拾行李，准备回上海工程技术大学装个大的");

        }
    }
}