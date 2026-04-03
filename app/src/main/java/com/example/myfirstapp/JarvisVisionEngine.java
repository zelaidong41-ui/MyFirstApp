package com.example.myfirstapp;

import android.content.Context;
import android.media.Image;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
// 注意这里引入的是 defaults（内置默认模型）
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

// 全局底层置信度阈值，默认 0.75

public class JarvisVisionEngine {

    private ImageLabeler labeler;

    public interface VisionCallback {
        void onFoodRecognized(String foodName, float confidence);
    }

    // 🧠 核心修改：使用内置的通用大脑，不再需要 tflite 文件！
    public void initialize(Context context) {
        // 使用默认选项，它会自动下载/使用内置的常识模型
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.5f) // 只要有 50% 的把握就说出来
                        .build();

        labeler = ImageLabeling.getClient(options);
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    public void analyzeFrame(androidx.camera.core.ImageProxy imageProxy, VisionCallback callback) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null || labeler == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    if (!labels.isEmpty()) {
                        // 拿最有把握的第一个结果
                        ImageLabel topLabel = labels.get(0);
                        String itemName = topLabel.getText();
                        float confidence = topLabel.getConfidence();

                        callback.onFoodRecognized(itemName, confidence);
                    }
                })
                .addOnFailureListener(e -> Log.e("JARVIS", "识别失败", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }
}