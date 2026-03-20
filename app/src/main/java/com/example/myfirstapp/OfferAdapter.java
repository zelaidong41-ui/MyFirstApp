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

        // 🌟 1. 核心拆解：一刀切开！parts[0] 是标题，parts[1] 是网址
        String rawData = offerList.get(position);
        String[] parts = rawData.split("@@@");
        String title = parts[0];
        // 万一旧数据没有网址，给个默认防崩网址
        String link = parts.length > 1 ? parts[1] : "https://www.wanandroid.com";

        // 🌟 2. 屏幕上只显示纯净的标题
        holder.tvOffer.setText(title);

        // ==========================================
        // 🌟 完美保留你的高级货：极客热词雷达！(根据纯标题识别)
        // ==========================================
        String imageUrl;
        String lowerCaseTitle = title.toLowerCase();

        if (lowerCaseTitle.contains("android") || lowerCaseTitle.contains("安卓")) {
            imageUrl = "https://ui-avatars.com/api/?name=And&background=3DDC84&color=fff&size=128";
        } else if (lowerCaseTitle.contains("java") || lowerCaseTitle.contains("spring")) {
            imageUrl = "https://ui-avatars.com/api/?name=Jav&background=f89820&color=fff&size=128";
        } else if (lowerCaseTitle.contains("flutter") || lowerCaseTitle.contains("dart")) {
            imageUrl = "https://ui-avatars.com/api/?name=Flu&background=02569B&color=fff&size=128";
        } else if (lowerCaseTitle.contains("面试") || lowerCaseTitle.contains("算法")) {
            imageUrl = "https://ui-avatars.com/api/?name=面&background=E53935&color=fff&size=128";
        } else {
            imageUrl = "https://avatars.githubusercontent.com/u/9919?v=4";
        }

        // 呼叫重武器 Glide 执行精准打击！
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .error(R.mipmap.ic_launcher)
                .into(holder.ivLogo);

        // 👇 3. 短按雷达改造：不用之前那个搜索链接了，带上真正的 link，踢进咱们刚刚精装修的 ArticleActivity！
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ArticleActivity.class);
            intent.putExtra("URL_KEY", link); // 行李箱里塞入真实的网址
            v.getContext().startActivity(intent);
        });

        // 👇 4. 完美保留你的长按毁灭装置！
        holder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
            @Override
            public boolean onLongClick(android.view.View v) {
                int currentPosition = holder.getAdapterPosition();
                offerList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                android.widget.Toast.makeText(v.getContext(), "已残忍拒绝：" + title, android.widget.Toast.LENGTH_SHORT).show();
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