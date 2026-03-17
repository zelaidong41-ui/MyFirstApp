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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 🌟 全局唯一的数据仓库！
    List<String> myGlobalOfferList;
    // 🌟 全局唯一的兵工厂（把两个 Adapter 合并成一个了！）
    OfferAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 去保险箱提货（保留本地垫底数据）
        myGlobalOfferList = loadOffersFromSafe();

        // 2. 找到屏幕上的列表控件
        RecyclerView myOfferList = findViewById(R.id.myOfferList);
        myOfferList.setLayoutManager(new LinearLayoutManager(this));

        // 3. 给全局唯一 adapter 注入灵魂，并立刻绑到屏幕上！
        myAdapter = new OfferAdapter(myGlobalOfferList);
        myOfferList.setAdapter(myAdapter);

        // ==========================================
        // 🌐 全新一代：Retrofit 真实网络请求引擎
        // ==========================================
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getArticles().enqueue(new retrofit2.Callback<WanResponse>() {
            @Override
            public void onResponse(retrofit2.Call<WanResponse> call, retrofit2.Response<WanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 拿到真实的战利品！
                    List<WanResponse.Article> realArticles = response.body().data.datas;

                    myGlobalOfferList.clear(); // 把保险箱里的旧数据清空

                    for (WanResponse.Article article : realArticles) {
                        myGlobalOfferList.add(article.title); // 塞入玩安卓真实的开源文章标题
                    }

                    // 极其关键：通知真正的 myAdapter 刷新屏幕！
                    myAdapter.notifyDataSetChanged();

                    // 顺手把从网上刚进的真货，存进你的本地保险箱里！
                    saveOffersToSafe(myGlobalOfferList);

                    Toast.makeText(MainActivity.this, "牛逼！连上真实服务器了！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<WanResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "网络请求失败，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });

        // ==========================================
        // 🎮 控制台：手动添加新 Offer
        // ==========================================
        EditText etNewOffer = findViewById(R.id.etNewOffer);
        Button btnAddOffer = findViewById(R.id.btnAddOffer);

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = etNewOffer.getText().toString();
                if (inputStr.trim().isEmpty()) { return; }

                myAdapter.addOffer(inputStr); // 呼叫真正的兵工厂加子弹
                saveOffersToSafe(myGlobalOfferList); // 🌟 双重保险：立刻锁进硬盘！

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
        String longString = TextUtils.join("###", listToSave);
        editor.putString("ALL_OFFERS", longString);
        editor.commit();
    }

    private List<String> loadOffersFromSafe() {
        SharedPreferences safe = getSharedPreferences("OfferSafe", MODE_PRIVATE);
        String longString = safe.getString("ALL_OFFERS", "");
        List<String> resultList = new ArrayList<>();
        if (!longString.isEmpty()) {
            String[] splitArray = longString.split("###");
            resultList.addAll(Arrays.asList(splitArray));
        }
        return resultList;
    }
}