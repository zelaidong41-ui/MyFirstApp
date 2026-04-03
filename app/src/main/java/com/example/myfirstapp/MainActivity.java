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

// 🚀 新增导入的包
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import android.widget.SeekBar;
import android.os.SystemClock;

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

    // 🚀 新增：控制底层 AI 识别敏感度的全局变量（默认 0.75）必须写在 class 里面！
    public static float CURRENT_CONFIDENCE_THRESHOLD = 0.75f;

    // 🚀 新增：绝招三用到的数组，记录点击时间
    private long[] mHits = new long[5];

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

        // ==========================================
        // 🚀 新增绝招二：点击“目标缺口”，丝滑呼出底部数据面板
        // ==========================================
        tvTargetProtein.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            bottomSheetDialog.setContentView(R.layout.layout_data_sheet);

            TextView tvData = bottomSheetDialog.findViewById(R.id.tv_nutrition_data);
            if (tvData != null) {
                // 动态获取真实数据，塞入抽屉面板
                SharedPreferences prefs = getSharedPreferences("JarvisPrefs", MODE_PRIVATE);
                float weight = prefs.getFloat("USER_WEIGHT", 70f);
                int level = prefs.getInt("USER_WORKOUT_LEVEL", 1);
                double target = JarvisNutritionEngine.calculateTargetProtein(weight, level);
                double gap = JarvisNutritionEngine.calculateProteinGap(target, currentConsumedProtein);

                tvData.setText(String.format(
                        "> 目标锁定：机体代谢监控\n> 总体重设定：%.1f kg\n> 当前已吸收：%.1f g\n> 剩余缺口：%.1f g\n> 状态：建议继续进食",
                        weight, currentConsumedProtein, gap));
            }
            bottomSheetDialog.show();
        });

        // ==========================================
        // 🚀 新增绝招三：对 J.A.R.V.I.S 建议 5 连击，召唤 ROOT 面板
        // ==========================================
        tvJarvisAdvice.setOnClickListener(v -> {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            // 如果 1000 毫秒内点满 5 次
            if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                showDeveloperPanel();
            }
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

        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastDate = prefs.getString("LAST_OPEN_DATE", "");

        if (!todayDate.equals(lastDate)) {
            prefs.edit()
                    .putFloat("USER_CONSUMED", 0f)
                    .putString("LAST_OPEN_DATE", todayDate)
                    .apply();
            Toast.makeText(this, "☀️ 太阳升起。机体代谢重置完毕，今日战斗开始！", Toast.LENGTH_LONG).show();
        }

        float weight = prefs.getFloat("USER_WEIGHT", 70f);
        int level = prefs.getInt("USER_WORKOUT_LEVEL", 1);
        currentConsumedProtein = prefs.getFloat("USER_CONSUMED", 0f);

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

                        // ==========================================
                        // 💥 终极硬核：在这里设卡拦截！
                        // ==========================================
                        if (confidence < CURRENT_CONFIDENCE_THRESHOLD) {
                            // 把握不够，直接 return，不刷新界面！
                            return;
                        }

                        String nutritionInfo = JarvisNutritionEngine.analyzeFoodFromVision(itemName);
                        double proteinNum = JarvisNutritionEngine.getProteinNumberFromVision(itemName);

                        runOnUiThread(() -> {
                            resultTextView.setText("视觉锁定: " + itemName + "\n" + nutritionInfo);

                            if (proteinNum > 0) {
                                currentScannedProtein = proteinNum;
                                btnConsume.setVisibility(View.VISIBLE);
                                btnConsume.setText("⚡ 确认吸收 [+" + proteinNum + "g 蛋白质]");
                                btnConsume.setTag(System.currentTimeMillis());
                            } else {
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

    // ==========================================
    // 🚀 进化版：能真正修改底层参数的开发者面板
    // ==========================================
    private void showDeveloperPanel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚠️ [ROOT] 开发者底层调参面板");

        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(100);
        // 💥 根据当前的真实阈值，设置滑动条的初始位置
        seekBar.setProgress((int) (CURRENT_CONFIDENCE_THRESHOLD * 100));

        builder.setView(seekBar);
        builder.setPositiveButton("注入新参数", (dialog, which) -> {

            // 拿到滑动条的值，转成 0.0 到 1.0 的小数
            float newThreshold = seekBar.getProgress() / 100f;

            // 💥 真正覆盖底层的全局变量！
            CURRENT_CONFIDENCE_THRESHOLD = newThreshold;

            Toast.makeText(MainActivity.this,
                    "J.A.R.V.I.S 底层视觉置信度阈值已覆盖为: " + CURRENT_CONFIDENCE_THRESHOLD,
                    Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
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