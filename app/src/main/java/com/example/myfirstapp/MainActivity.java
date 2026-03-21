package com.example.myfirstapp; // ⚠️ 注意保留你自己的包名

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 1. 声明咱们大盘上的两块显示屏
    private TextView tvTotalProtein;
    private TextView tvWorkoutLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 绑定刚才画的暗黑 UI

        // 2. 找到屏幕上的显示控件
        tvTotalProtein = findViewById(R.id.tvTotalProtein);
        tvWorkoutLog = findViewById(R.id.tvWorkoutLog);

        // 3. 极客启动指令：唤醒后台影子刺客，去底层拿数据点亮屏幕！
        loadFitnessDashboard();
    }

    // ==========================================
    // 🌟 极客大盘驱动引擎
    // ==========================================
    private void loadFitnessDashboard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取数据库底座
                    AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
                    WorkoutDao workoutDao = db.workoutDao();
                    DietDao dietDao = db.dietDao();

                    String today = "2026-03-21";

                    // ---------- [防御性预充填数据：防止库里没东西] ----------
                    // 如果今天还没记录，咱们先塞一条进去，保证界面有东西看
                    if (workoutDao.getWorkoutsByDate(today).isEmpty()) {
                        workoutDao.insertWorkout(new WorkoutEntity(today, 70.5f, "胸肌 + 肱三头肌", 8, "撕裂痛，最后一个推举完全力竭"));
                        dietDao.insertDiet(new DietEntity(today, "水煮鸡胸肉", 31.0f, 2.5f, 31.0f * 2.5f));
                    }
                    // --------------------------------------------------

                    // 🏆 1. 结算今天的总蛋白质
                    float totalProtein = dietDao.getTotalProteinForToday(today);

                    // 📝 2. 获取今天的训练日志
                    List<WorkoutEntity> todaysWorkouts = workoutDao.getWorkoutsByDate(today);
                    String workoutText = "今天躺平没练铁";
                    if (!todaysWorkouts.isEmpty()) {
                        WorkoutEntity w = todaysWorkouts.get(0); // 拿今天的第一条记录
                        workoutText = w.bodyPart + " | 痛觉强度: " + w.intensity + "\n\n体感：" + w.sensationFeel;
                    }

                    // 🌟 3. UI 刷新指令必须在主线程执行！
                    final String finalWorkoutText = workoutText;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 点亮屏幕！
                            tvTotalProtein.setText(String.valueOf(totalProtein));
                            tvWorkoutLog.setText(finalWorkoutText);
                        }
                    });

                } catch (Exception e) {
                    Log.e("GeekFitness", "❌ 界面渲染翻车: " + e.getMessage());
                }
            }
        }).start();
    }
}