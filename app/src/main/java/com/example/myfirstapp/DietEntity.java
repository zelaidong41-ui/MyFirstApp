package com.example.myfirstapp; // ⚠️ 注意核对你的包名

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diet_table")
public class DietEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date; // 饮食日期

    // ==========================================
    // 🛡️ 极客防御：应对 AI 视觉局限的核心战备区 🛡️
    // ==========================================

    public String recognizedFoodName; // 🌟 1. 改名：这是AI单纯识别出来的物品“名字”（例如："鸡胸肉"）

    public float perUnitProtein; // 🌟 2. 改名：这是从本地数据库查出的该物品“单位含量”（例如：每100g含20g蛋白）

    /* * 🌟 3. 新增核弹级字段：用户手动修正数量 (Human-in-the-Loop)
     * 【重要简历加分项】我们在 UI 界面上会这样设计：
     * AI识别到鸡胸肉后，我们会弹出一个极其显眼的“滑块”或“输入框”，
     * 诚实地告诉用户：“AI无法预估重量，请您手动调整克数（默认100g）”。
     * 这个字段就存用户最终确定的数量（例如：他把滑块拉到了 2.5，表示他吃了 250g）。
     */
    public float quantityMultiplier; // 用户调整的倍数（默认1.0表示100g，1.5表示150g，以此类推）

    public float finalProtein; // 🌟 4. 新增：这是perUnitProtein * quantityMultiplier 计算出来的“最终真实摄入量”！用于最后算总账！

    // 构造函数：注入灵魂（记得构造函数也要跟着字段名一起更新！）
    public DietEntity(String date, String recognizedFoodName, float perUnitProtein, float quantityMultiplier, float finalProtein) {
        this.date = date;
        this.recognizedFoodName = recognizedFoodName;
        this.perUnitProtein = perUnitProtein;
        this.quantityMultiplier = quantityMultiplier;
        this.finalProtein = finalProtein;
    }
}