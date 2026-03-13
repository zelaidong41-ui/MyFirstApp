package com.example.myfirstapp;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> myGlobalOfferList;
    OfferAdapter adapter;

    // 🌟 全局唯一的数据仓库！
    private OfferAdapter myAdapter;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
// 1. 去保险箱提货（保留这一句，把本地的存货先拿出来垫底）
            myGlobalOfferList = loadOffersFromSafe();

            // ==========================================
            // 🚨 极其关键的抢救：在这里插队！必须先造出 adapter！
            // ==========================================
            RecyclerView myOfferList = findViewById(R.id.myOfferList);
            myOfferList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

            adapter = new OfferAdapter(myGlobalOfferList); // 👈 给全局 adapter 注入灵魂！
            myOfferList.setAdapter(adapter);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build();


            ApiService apiService = retrofit.create(ApiService.class);

            apiService.getArticles().enqueue(new retrofit2.Callback<WanResponse>() {
                @Override
                public void onResponse(retrofit2.Call<WanResponse> call, retrofit2.Response<WanResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        java.util.List<WanResponse.Article> realArticles = response.body().data.datas;

                        // 🚨 注意：这里换成了你自己的变量名 myGlobalOfferList
                        myGlobalOfferList.clear(); // 把保险箱里的旧数据清空

                        for (WanResponse.Article article : realArticles) {
                            myGlobalOfferList.add(article.title); // 塞入玩安卓真实的开源文章标题
                        }

                        // 极其关键：通知 Adapter 刷新屏幕！
                        adapter.notifyDataSetChanged();

                        // 顺手把从网上刚进的真货，存进你的本地保险箱里！
                        saveOffersToSafe(myGlobalOfferList);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<WanResponse> call, Throwable t) {
                    android.widget.Toast.makeText(MainActivity.this, "网络请求失败，请检查网络！", android.widget.Toast.LENGTH_SHORT).show();
                }
            });

            // ==========================================
            // 👇 下面是你原来绑定 RecyclerView 和 Adapter 的代码，一行都千万别动！
            // RecyclerView recyclerView = findViewById(R.id.recyclerView);
            // adapter = new OfferAdapter(myGlobalOfferList);
            // ...



        // 3. 组装机关枪和兵工厂
        myAdapter = new OfferAdapter(myGlobalOfferList);

        // 4. 控制台：添加新 Offer
        EditText etNewOffer = findViewById(R.id.etNewOffer);
        Button btnAddOffer = findViewById(R.id.btnAddOffer);

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = etNewOffer.getText().toString();
                if (inputStr.trim().isEmpty()) { return; }

                myAdapter.addOffer(inputStr); // 呼叫兵工厂加子弹
                saveOffersToSafe(myGlobalOfferList); // 🌟 双重保险：只要加了新数据，立刻锁进硬盘！

                myOfferList.scrollToPosition(0);
                etNewOffer.setText("");
            }
        });
    }

    // 🌟 拦截临终遗言：退到后台时，再存一次！
    @Override
    protected void onPause() {
        super.onPause();
        if (myGlobalOfferList != null) {
            saveOffersToSafe(myGlobalOfferList);
        }
    }

    // ==========================================
    // 👇 绝对靠谱的本地保险箱技术 👇
    // ==========================================
    private void saveOffersToSafe(List<String> listToSave) {
        SharedPreferences safe = getSharedPreferences("OfferSafe", MODE_PRIVATE);
        SharedPreferences.Editor editor = safe.edit();
        // 缝合怪战术：用 ### 连起来
        String longString = TextUtils.join("###", listToSave);
        editor.putString("ALL_OFFERS", longString);
        editor.commit(); // 强制立刻写入硬盘！
    }

    private List<String> loadOffersFromSafe() {
        SharedPreferences safe = getSharedPreferences("OfferSafe", MODE_PRIVATE);
        String longString = safe.getString("ALL_OFFERS", "");
        List<String> resultList = new ArrayList<>();
        if (!longString.isEmpty()) {
            // 切西瓜战术：用 ### 劈开
            String[] splitArray = longString.split("###");
            resultList.addAll(Arrays.asList(splitArray));
        }
        return resultList;
    }
}