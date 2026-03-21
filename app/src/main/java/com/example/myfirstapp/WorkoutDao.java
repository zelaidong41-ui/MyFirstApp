package com.example.myfirstapp; // ⚠️ 核对包名

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WorkoutDao {

    // 🌟 极客操作 1：一键插入当天的训练记录
    @Insert
    void insertWorkout(WorkoutEntity workout);

    // 🌟 极客操作 2：查询某一天所有的训练记录（用于在 UI 上展示今天练了啥）
    @Query("SELECT * FROM workout_table WHERE date = :targetDate")
    List<WorkoutEntity> getWorkoutsByDate(String targetDate);

    // 🌟 极客操作 3：查询所有的历史记录
    @Query("SELECT * FROM workout_table ORDER BY id DESC")
    List<WorkoutEntity> getAllWorkouts();
}