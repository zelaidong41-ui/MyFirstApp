package com.example.myfirstapp;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // 👇 发起 GET 请求，瞄准玩安卓的“首页文章列表”接口
    @GET("article/list/0/json")
    Call<WanResponse> getArticles();

}