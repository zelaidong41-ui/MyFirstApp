package com.example.myfirstapp; // ⚠️ 核对包名

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DietDao {

    // 🌟 往肚子里塞食物（插入记录）
    @Insert
    void insertDiet(DietEntity diet);

    // 🌟 查询今天到底吃了多少顿（用于核算总蛋白质）
    @Query("SELECT * FROM diet_table WHERE date = :targetDate")
    List<DietEntity> getDietsByDate(String targetDate);

    // 🌟 极客终极核武：直接让 SQLite 数据库帮你把今天吃的所有蛋白质加起来！
    // 这样你就不用在 Java 代码里写 for 循环去加了，性能直接拉满！
    @Query("SELECT SUM(finalProtein) FROM diet_table WHERE date = :targetDate")
    float getTotalProteinForToday(String targetDate);
}