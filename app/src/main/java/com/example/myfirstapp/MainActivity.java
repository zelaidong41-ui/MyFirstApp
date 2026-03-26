package com.example.myfirstapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private TextView resultTextView;
    private TextView tvTargetProtein;
    private TextView tvJarvisAdvice;
    private TextView tvWorkoutLog;
    private Button btnConsume; // 🎯 我们的战术吸收按钮！

    private ExecutorService cameraExecutor;
    private JarvisVisionEngine jarvisEngine;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    // 动态变量
    private double currentScannedProtein = 0.0;
    private double currentConsumedProtein = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);
        resultTextView = findViewById(R.id.resultTextView);
        tvTargetProtein = findViewById(R.id.tvTargetProtein);
        tvJarvisAdvice = findViewById(R.id.tvJarvisAdvice);
        tvWorkoutLog = findViewById(R.id.tvWorkoutLog);
        btnConsume = findViewById(R.id.btnConsume);

        // 初始化界面并刷新数据
        refreshNutritionPanel();

        // 提示用户去启动页修改强度
        tvWorkoutLog.setOnClickListener(v -> {
            Toast.makeText(this, "Sir, 痛感强度请在启动系统时确认，中途切勿更改！", Toast.LENGTH_SHORT).show();
        });

        // 💥 终极交互：点击吸收按钮！
        btnConsume.setOnClickListener(v -> {
            if (currentScannedProtein > 0) {
                // 1. 加到肚子里
                currentConsumedProtein += currentScannedProtein;

                // 2. 存进本地记忆
                SharedPreferences prefs = getSharedPreferences("JarvisPrefs", MODE_PRIVATE);
                prefs.edit().putFloat("USER_CONSUMED", (float) currentConsumedProtein).apply();

                // 3. 瞬间刷新头顶的缺口数据
                refreshNutritionPanel();
                Toast.makeText(this, "✅ 能量吸收完毕！蛋白质补给 +" + currentScannedProtein + "g", Toast.LENGTH_SHORT).show();

                // 4. 重置状态，隐藏按钮
                currentScannedProtein = 0.0;
                btnConsume.setVisibility(View.GONE);
                btnConsume.setTag(null); // 清除锁定时间戳
            }
        });

        jarvisEngine = new JarvisVisionEngine();
        jarvisEngine.initialize(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    // --- 📊 刷新头顶战术面板 ---
    private void refreshNutritionPanel() {
        SharedPreferences prefs = getSharedPreferences("JarvisPrefs", MODE_PRIVATE);

        // ⏳ 1. 核心进化：时间感知与跨天清零逻辑
        // 获取今天的日期，例如 "2026-03-26"
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        // 获取系统记忆里上次记录的日期，如果没记过，默认给个空
        String lastDate = prefs.getString("LAST_OPEN_DATE", "");

        // 判断：如果今天和上次记忆的日期不一样，说明跨天了！
        if (!todayDate.equals(lastDate)) {
            prefs.edit()
                    .putFloat("USER_CONSUMED", 0f) // 瞬间清空肚子里的蛋白质
                    .putString("LAST_OPEN_DATE", todayDate) // 把今天记入日历，防止重复清空
                    .apply();

            // 给你一个硬核的早晨问候
            Toast.makeText(this, "☀️ 太阳升起。机体代谢重置完毕，今日战斗开始！", Toast.LENGTH_LONG).show();
        }

        // 🧠 2. 读取最新档案
        float weight = prefs.getFloat("USER_WEIGHT", 70f);
        int level = prefs.getInt("USER_WORKOUT_LEVEL", 1);
        // 如果上面触发了跨天清零，这里读出来的就会是新鲜的 0f
        currentConsumedProtein = prefs.getFloat("USER_CONSUMED", 0f);

        // 📈 3. 计算与刷新 UI
        double targetProtein = JarvisNutritionEngine.calculateTargetProtein(weight, level);
        double gap = JarvisNutritionEngine.calculateProteinGap(targetProtein, currentConsumedProtein);
        String advice = JarvisNutritionEngine.getJarvisAdvice(targetProtein, currentConsumedProtein);

        tvTargetProtein.setText(String.format("今日营养缺口: 需补充 %.1fg 蛋白质", gap));
        tvJarvisAdvice.setText(advice);
        tvWorkoutLog.setText(String.format("SYS_LOG: 已摄入 %.1fg / 目标 %.1fg", currentConsumedProtein, targetProtein));
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalyzer.setAnalyzer(cameraExecutor, imageProxy -> {
                    jarvisEngine.analyzeFrame(imageProxy, (itemName, confidence) -> {
                        // 提取文字和数字
                        String nutritionInfo = JarvisNutritionEngine.analyzeFoodFromVision(itemName);
                        double proteinNum = JarvisNutritionEngine.getProteinNumberFromVision(itemName);

                        // 更新 UI
                        runOnUiThread(() -> {
                            resultTextView.setText("视觉锁定: " + itemName + "\n" + nutritionInfo);

                            // 🧠 如果发现了有蛋白质的东西
                            if (proteinNum > 0) {
                                currentScannedProtein = proteinNum;
                                btnConsume.setVisibility(View.VISIBLE);
                                btnConsume.setText("⚡ 确认吸收 [+" + proteinNum + "g 蛋白质]");
                                // ⏳ 记录锁定目标的时间（防闪烁机制）
                                btnConsume.setTag(System.currentTimeMillis());
                            } else {
                                // 🛡️ 防闪烁护盾：如果视野里没食物了，检查是不是过了 3 秒
                                Long lastTime = (Long) btnConsume.getTag();
                                if (lastTime != null && (System.currentTimeMillis() - lastTime > 3000)) {
                                    btnConsume.setVisibility(View.GONE);
                                    btnConsume.setTag(null);
                                    currentScannedProtein = 0.0;
                                }
                            }
                        });
                    });
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}