package com.example.myfirstapp;

import java.util.List;

public class WanResponse {
    // 第一层：抓取核心的 data 数据块
    public WanData data;

    // 第二层：解析 data 内部的结构
    public static class WanData {
        // 抓取名为 datas 的列表，里面装的全是文章
        public List<Article> datas;
    }

    // 第三层：定义每一篇文章的具体内容
    public static class Article {
        // 我们只需要文章的标题！
        public String title;
    }
}