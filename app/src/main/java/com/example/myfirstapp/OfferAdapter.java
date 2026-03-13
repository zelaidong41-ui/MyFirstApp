package com.example.myfirstapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// 1. 继承官方的 Adapter 模板
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    // 存放我们所有大厂 Offer 数据的仓库（一个文字列表）
    private List<String> offerList;

    // 兵工厂的构造函数：外面把数据送进来，我们就存到仓库里
    public OfferAdapter(List<String> offerList) {
        this.offerList = offerList;
    }

    // 核心步骤 1：找图纸（把我们刚画的 item_offer.xml 子弹图纸拿过来）
    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }
    // 核心步骤 2：填火药（把真实的 Offer 名字，写到子弹的 TextView 上）
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        // 现在拉下来的是文章标题了！
        String realOffer = offerList.get(position);
        holder.tvOffer.setText(realOffer);

        // 给整个卡片设置点击监听器
        holder.itemView.setOnClickListener(v -> {

            // 1. 既然只有标题，咱们就把它拼成一个搜索链接！
            // 比如咱们直接去玩安卓站内搜索这个标题：
            String searchUrl = "https://www.wanandroid.com/article/query?k=" + realOffer;

            // 2. 打包 Intent 行囊，写明目的地是 DetailActivity
            Intent intent = new Intent(v.getContext(), DetailActivity.class);

            // 3. 把拼接好的搜索网址塞进行囊
            intent.putExtra("ARTICLE_URL", searchUrl);

            // 4. 发射！跳转页面！
            v.getContext().startActivity(intent);
        });
        // ==========================================
        // 🌟 全新一代智能分拣机：极客热词雷达！
        // ==========================================
        String imageUrl;

        // 为了防止大小写带来的漏网之鱼，先把标题统一转换成小写！
        String lowerCaseTitle = realOffer.toLowerCase();

        // 1. 捕捉 Android 或 安卓（使用经典的安卓绿 #3DDC84）
        if (lowerCaseTitle.contains("android") || lowerCaseTitle.contains("安卓")) {
            imageUrl = "https://ui-avatars.com/api/?name=And&background=3DDC84&color=fff&size=128";

            // 2. 捕捉 Java 或 Spring（使用经典的 Java 橙 #f89820）
        } else if (lowerCaseTitle.contains("java") || lowerCaseTitle.contains("spring")) {
            imageUrl = "https://ui-avatars.com/api/?name=Jav&background=f89820&color=fff&size=128";

            // 3. 捕捉 Flutter 或 Dart（使用专属的跨平台蓝 #02569B）
        } else if (lowerCaseTitle.contains("flutter") || lowerCaseTitle.contains("dart")) {
            imageUrl = "https://ui-avatars.com/api/?name=Flu&background=02569B&color=fff&size=128";

            // 4. 捕捉 面试 或 算法（面试题专属红色警戒 #E53935）
        } else if (lowerCaseTitle.contains("面试") || lowerCaseTitle.contains("算法")) {
            imageUrl = "https://ui-avatars.com/api/?name=面&background=E53935&color=fff&size=128";

            // 5. 如果上面的热词都没有命中
        } else {
            // 保底方案：依然使用你最熟悉的 GitHub 黑白极客猫咪！
            imageUrl = "https://avatars.githubusercontent.com/u/9919?v=4";
        }

        // 呼叫重武器 Glide 执行精准打击！
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .into(holder.ivLogo);

        // 1. 短按雷达：接受 Offer（穿越到欢迎页）
        // 1. 真正的跳转：去 WebView 网页页！
        holder.itemView.setOnClickListener(v -> {
            String searchUrl = "https://www.wanandroid.com/article/query?k=" + realOffer;
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("ARTICLE_URL", searchUrl);
            v.getContext().startActivity(intent);
        });

        // 👇 2. 长按毁灭装置：拒绝 Offer（从列表里踢出去） 👇
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 第一步：精确定位！获取当前被长按的是第几个卡片
                int currentPosition = holder.getAdapterPosition();

                // 第二步：从咱们的真实仓库里，把这个 Offer 彻底删掉
                offerList.remove(currentPosition);

                // 第三步：核心面试考点！通知兵工厂这个位置的子弹被销毁了，让后面的排上来！
                notifyItemRemoved(currentPosition);

                // 第四步：弹个提示爽一下
                Toast.makeText(v.getContext(), "已残忍拒绝：" + realOffer, Toast.LENGTH_SHORT).show();

                // 返回 true 表示我们已经处理完这个长按动作了
                return true;

            }
        });
    }

    // 核心步骤 3：清点弹药（告诉机关枪我们一共有多少个 Offer）
    @Override
    public int getItemCount() {
        return offerList.size();
    }

    // 内部类：用来死死抓住那颗子弹里的 TextView（防止每次都要重新找，提高性能）
    public class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView tvOffer;
        android.widget.ImageView ivLogo;//1 声明相框
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOffer = itemView.findViewById(R.id.tvOfferItem); // 抓住刚刚造的子弹里的那个大字
            ivLogo = itemView.findViewById(R.id.ivLogo);//2 绑定图纸上的logo
        }
    }
    //新加的紧急进货功能
    public void addOffer(String newOffer){
        //1 把新拿到的offer塞进仓库的最里面（第0个位置）
        offerList.add(0,newOffer);
        //2 终极面试考点：通知兵工厂，第0个位置有新子弹插队了，赶紧播放插入动画
        notifyItemInserted(0);
    }
}