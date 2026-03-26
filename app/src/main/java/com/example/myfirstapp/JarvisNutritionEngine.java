package com.example.myfirstapp;

public class JarvisNutritionEngine {

    // 1. 计算今日总目标
    public static double calculateTargetProtein(float weight, int level) {
        // 基础代谢需要 1.2g/kg，加上训练强度带来的额外损耗
        double baseProtein = weight * 1.2;
        double extraProtein = level * 5.0; // 每增加 1 级强度，多吃 5g 蛋白质
        return baseProtein + extraProtein;
    }

    // 2. 计算当前缺口
    public static double calculateProteinGap(double target, double consumed) {
        double gap = target - consumed;
        return gap > 0 ? gap : 0.0;
    }

    // 🧠 3. 史诗级升级：基于战前储备的智能战术建议！
    public static String getJarvisAdvice(double target, double consumed) {
        double gap = target - consumed;

        // 已经吃够了
        if (gap <= 0) {
            return "✅ 目标达成：机体修复矩阵运转达到峰值，多余氨基酸将转化为储备能源。";
        }

        // 计算摄入比例
        double ratio = consumed / target;

        // 状态 A：战前补给极其充沛 (已吃够 60% 以上)
        if (ratio >= 0.6) {
            return "🛡️ 战前补给充沛：血液氨基酸浓度良好，训练后无需紧急摄入，睡前填补剩余的 " + String.format("%.1f", gap) + "g 即可。";
        }
        // 状态 B：吃了一点，但不多 (处于 1% 到 59% 之间)
        else if (ratio > 0) {
            return "🔋 能量储备中等：机体正在消耗库存，建议在训练后 45 分钟的「合成代谢窗口期」内补充优质蛋白，当前缺口 " + String.format("%.1f", gap) + "g。";
        }
        // 状态 C：一口没吃，纯空腹！(0%)
        else {
            return "⚠️ 极度警告：空腹高消耗状态！肌肉蛋白面临分解风险（糖异生），请在 30 分钟内立刻摄入快速吸收的蛋白质！当前总缺口 " + String.format("%.1f", gap) + "g。";
        }
    }

    // 4. 视觉识别反馈（文字版）
    // 4. 视觉识别反馈（文字版 - 支持中英文）
    // 4. 视觉识别反馈（文字版 - 新增模糊估算兜底）
    public static String analyzeFoodFromVision(String itemName) {
        String name = itemName.toLowerCase();
        if (name.contains("meat") || name.contains("chicken") || name.contains("beef") || name.contains("pork") ||
                name.contains("肉") || name.contains("鸡") || name.contains("牛") || name.contains("鱼")) {
            return "高维生物纤维锁定。预计可提供极佳的氨基酸矩阵。";
        } else if (name.contains("egg") || name.contains("蛋")) {
            return "微型蛋白质胶囊锁定。吸收率极高。";
        } else if (name.contains("food") || name.contains("dish") || name.contains("meal") || name.contains("cuisine") || name.contains("ingredient")) {
            // 🎯 新增兜底：认不出具体种类，但知道是吃的
            return "检测到未知食物混合物。已启用模糊估算协议。";
        }
        return "未检测到高密度蛋白质储备，不建议作为主要补给。";
    }

    // 5. 视觉识别反馈（数字版 - 触发按钮的核心开关！）
    public static double getProteinNumberFromVision(String itemName) {
        String name = itemName.toLowerCase();
        if (name.contains("meat") || name.contains("chicken") || name.contains("beef") || name.contains("pork") ||
                name.contains("肉") || name.contains("鸡") || name.contains("牛") || name.contains("鱼")) {
            return 30.0;
        } else if (name.contains("egg") || name.contains("蛋")) {
            return 6.0;
        } else if (name.contains("food") || name.contains("dish") || name.contains("meal") || name.contains("cuisine") || name.contains("ingredient")) {
            // 🎯 新增兜底：只要是食物，就给个 15g 均值，让按钮弹出来！
            return 15.0;
        }
        return 0.0;
    }
}