package com.example.myfirstapp; // ⚠️ 注意：这行必须是你自己的包名！

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // 1. 准备大厅的基础装备
    private List<String> myGlobalOfferList = new ArrayList<>();
    private OfferAdapter myAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. 找到屏幕上的 UI 控件
        RecyclerView myOfferList = findViewById(R.id.myOfferList);
        swipeRefresh = findViewById(R.id.swipeRefresh); // 确保你的 xml 里有这个下拉刷新控件

        // 3. 组装兵工厂 (RecyclerView)
        myOfferList.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new OfferAdapter(myGlobalOfferList);
        myOfferList.setAdapter(myAdapter);

        // ==========================================
        // 🌟 离线秒开黑科技：App一启动，立刻去本地仓库提货！
        // ==========================================
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
                List<ArticleEntity> savedArticles = db.articleDao().getAllArticles();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (savedArticles != null && !savedArticles.isEmpty()) {
                            myGlobalOfferList.clear();
                            for (ArticleEntity entity : savedArticles) {
                                myGlobalOfferList.add(entity.title + "@@@" + entity.link);
                            }
                            myAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "⚡ 已加载本地智能缓存", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

        // 4. 下拉刷新兵营：只要用户一拉，就去服务器进货
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataFromServer();
            }
        });

        // 5. 首次打开 App，自动触发一次网络请求抓新数据
        fetchDataFromServer();
    }

    // ==========================================
    // 🌐 独立出来的网络引擎：彻底告别大括号地狱！
    // ==========================================
    private void fetchDataFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getArticles().enqueue(new Callback<WanResponse>() {
            @Override
            public void onResponse(Call<WanResponse> call, Response<WanResponse> response) {
                // 收到消息，立刻把转圈圈停下
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<WanResponse.Article> realArticles = response.body().data.datas;

                    // A. 更新前台屏幕
                    myGlobalOfferList.clear();
                    for (WanResponse.Article article : realArticles) {
                        myGlobalOfferList.add(article.title + "@@@" + article.link);
                    }
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "牛逼，连上服务器并存入 Room！", Toast.LENGTH_SHORT).show();

                    // B. 派人去后厨（Room）存货
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
                            ArticleDao dao = db.articleDao();
                            dao.deleteAll(); // 清空旧货
                            for (WanResponse.Article article : realArticles) {
                                dao.insertArticle(new ArticleEntity(article.title, article.link)); // 塞入新货
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<WanResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, "网络崩了，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}