package com.example.myfirstapp; // ⚠️ 注意：这行必须是你自己的包名！

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// 🌟 魔法标签 1：@Dao 告诉 Room，这是一个机械臂控制台！
@Dao
public interface ArticleDao {

    // 🦾 按钮 1：进货（把一篇文章塞进仓库）
    // OnConflictStrategy.REPLACE 意思是：如果仓库里已经有这篇一模一样的文章了，直接替换掉旧的，防止报错！
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(ArticleEntity article);

    // 🦾 按钮 2：查库存（把仓库里所有的文章全拿出来）
    // 这里写了一句最基础的 SQL 语句：SELECT * FROM 表名 ORDER BY id DESC (按时间倒序排，最新的在上面)
    @Query("SELECT * FROM article_table ORDER BY id DESC")
    List<ArticleEntity> getAllArticles();

    // 🦾 按钮 3：销毁（从仓库里精确删掉某篇文章）
    @Delete
    void deleteArticle(ArticleEntity article);

    // 🦾 按钮 4：一键清仓（清空整个仓库，咱们测试或者用户点“清除缓存”时用得着）
    @Query("DELETE FROM article_table")
    void deleteAll();
}