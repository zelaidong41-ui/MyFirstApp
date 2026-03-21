package com.example.myfirstapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// 🌟 魔法标签升级：现在咱们的仓库里有三种包装箱了！
// 极其重要：用逗号把 WorkoutEntity.class 和 DietEntity.class 加进来！
@Database(entities = {ArticleEntity.class, WorkoutEntity.class, DietEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 🦾 暴露旧的机械臂：用来拿资讯文章（保持不变）
    public abstract ArticleDao articleDao();

    // 🦾 🌟 新增的极客机械臂：用来操控咱们的赛博健身教练！
    public abstract WorkoutDao workoutDao();
    public abstract DietDao dietDao();

    // ==========================================
    // 🌟 下面的单例模式代码完美无瑕，一字不改，直接沿用！
    // ==========================================
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // 加一把多线程安全锁，防止两个线程同时跑来建仓库
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // 🌟 核心建设指令：呼叫 Room 包工头建库！
                    // 因为你已经把旧 App 卸载了，这里 version = 1 不会报错，系统会直接按包含三张表的新图纸建一个全新的库！
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "wan_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}