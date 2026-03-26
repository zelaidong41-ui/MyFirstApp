package com.example.myfirstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DailyCheckInActivity extends AppCompatActivity {

    private int currentLevel = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("JarvisPrefs", MODE_PRIVATE);
        float savedWeight = prefs.getFloat("USER_WEIGHT", -1f);
        if (savedWeight == -1f) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_daily_checkin);

        TextView tvLevelNumber = findViewById(R.id.tvLevelNumber);
        TextView tvLevelDescription = findViewById(R.id.tvLevelDescription);
        SeekBar seekBarLevel = findViewById(R.id.seekBarLevel);
        Button btnEnterMain = findViewById(R.id.btnEnterMain);

        seekBarLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentLevel = progress;
                tvLevelNumber.setText("LEVEL: " + currentLevel);

                // 💥 史诗级硬核痛感词典升级！
                String description = "";
                switch (currentLevel) {
                    case 1: description = "休眠模式：全天无主动消耗。肌肉纤维处于绝对静息状态，今日运动量大概只够消化一杯冰水。"; break;
                    case 2: description = "关节润滑：极轻微活动。心率略微上浮，进行了基础拉伸或慢走，肌肉纤维未发生实质性断裂。"; break;
                    case 3: description = "轻微唤醒：基础充血。肌肉微微发酸，像被轻柔捶打。产生了少量乳酸，呼吸系统未感受到明显压力。"; break;
                    case 4: description = "常规泵感：中度充血。目标肌肉明显膨胀，呼吸加快。体验到了舒适的肌肉发力感，但远未达到力竭。"; break;
                    case 5: description = "标准撕裂：结构性微创。真实的训练开始！目标肌肉酸痛感强烈，最后几组表情逐渐狰狞，肌纤维已产生有效破坏。"; break;
                    case 6: description = "深度透支：糖原告急。大重量带来的深度刺激，强烈的乳酸堆积导致肌肉发胀发硬，组间休息时眼神开始涣散。"; break;
                    case 7: description = "严重破坏：力竭警告！极度酸爽的体验。最后几个动作全靠嘶吼和借力，放下器械的瞬间目标肌肉群止不住地颤抖。"; break;
                    case 8: description = "突破极限：神经疲劳。榨干最后一丝力气，中枢神经系统高度受压。痛感剧烈，下楼梯或拿水杯的动作系统已受阻。"; break;
                    case 9: description = "钢铁意志：全面重创。肌纤维遭遇毁灭性打击！动作彻底变形，视野发黑，感觉身体被彻底掏空，急需高能碳水注入。"; break;
                    case 10: description = "机体宕机：系统濒临崩溃！Sir，已达到人类生理极限。肌肉濒临溶解边缘，机体强制进入保护状态，请立刻停止呼吸以外的动作！"; break;
                }
                tvLevelDescription.setText(description);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnEnterMain.setOnClickListener(v -> {
            prefs.edit().putInt("USER_WORKOUT_LEVEL", currentLevel).apply();
            Toast.makeText(this, "损伤评级 " + currentLevel + " 录入完毕，正在校准营养目标...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}