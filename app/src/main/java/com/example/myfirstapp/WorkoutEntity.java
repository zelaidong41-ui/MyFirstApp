package com.example.myfirstapp; // ⚠️ 注意核对你的包名

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 告诉 Room：给我建一张名为 workout_table 的表
@Entity(tableName = "workout_table")
public class WorkoutEntity {

    @PrimaryKey(autoGenerate = true)
    public int id; // 数据的唯一编号

    public String date; // 训练日期

    public float bodyWeight; // 当天体重 (kg)

    public String bodyPart; // 训练部位 (例如："胸/肩/三头")

    /* * 🌟 赛博健身核心算法：训练强度痛觉指数 (1-10级)
     * 【1-3级】微流汗：轻微活动，肌肉毫无感觉。
     * 【4-5级】热身泵感：肌肉轻微发酸，有被“轻微捶打”的钝痛感，还能轻松继续。
     * 【6-7级】深度充血：强烈的紧绷感和酸胀感，动作开始变形，目标肌肉像灌了铅。
     * 【8-9级】力竭边缘：肌肉不受控制地发抖，出现明显的“撕裂痛”，绝对无法独立完成下一个标准动作。
     * 【10级】 绝对极限：灵魂出窍，疼到穿脱衣服都费劲，完全瘫痪。
     */
    public int intensity; // 存入 1-10 的数字，用于后台计算蛋白质乘数

    public String sensationFeel; // 🌟 新增：肌肉真实体感，存入如 "撕裂痛"、"轻微发酸"，以后喂给云端大模型做分析！

    // 构造函数：注入灵魂
    public WorkoutEntity(String date, float bodyWeight, String bodyPart, int intensity, String sensationFeel) {
        this.date = date;
        this.bodyWeight = bodyWeight;
        this.bodyPart = bodyPart;
        this.intensity = intensity;
        this.sensationFeel = sensationFeel;
    }
}