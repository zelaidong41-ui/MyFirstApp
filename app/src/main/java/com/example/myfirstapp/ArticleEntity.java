package com.example.myfirstapp; // ⚠️ 注意：这行必须是你自己的包名！

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 🌟 魔法标签 1：@Entity 告诉 Room，这是一个要在数据库里建表的包装箱
// tableName 就是给这张表起个响亮的名字
@Entity(tableName = "article_table")
public class ArticleEntity {

    // 🌟 魔法标签 2：@PrimaryKey 告诉 Room，这是货物的唯一条形码（主键）
    // autoGenerate = true 意思是：让仓库管理员自动从 1 开始贴号，咱们不用操心！
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 下面这两个就是咱们用来装货的普通口袋
    public String title;
    public String link;

    // 💡 极客小贴士：为了方便以后咱们快速把标题和网址塞进箱子，咱们给它加个“快捷打包机”（构造函数）
    public ArticleEntity(String title, String link) {
        this.title = title;
        this.link = link;
    }
}