package com.noor.newease;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PostsAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Ticket> listTickets;

    public PostsAdapter(Context mContext) {
        this.mContext = mContext;
        this.listTickets = new ArrayList<>();
    }

    public void updateListTickets(List<Ticket> list) {
        this.listTickets.clear();
        this.listTickets.addAll(list);
       notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostVH(LayoutInflater.from(mContext).inflate(R.layout.tweets_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostVH postVH = (PostVH) holder;

        postVH.tvPostText.setText(listTickets.get(position).getTweetText());
        postVH.tvUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if (TextUtils.isEmpty(listTickets.get(position).getTweetImageURL())){
            postVH.imgPost.setVisibility(View.GONE);
        }else {
            postVH.imgPost.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(listTickets.get(position).getTweetImageURL()).into(postVH.imgPost);
        }
        Glide.with(mContext).load(listTickets.get(position).getPersonImage()).into(postVH.imgUser);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyy hh:mm ", Locale.getDefault());
            String date = simpleDateFormat.format(new Date(listTickets.get(position).getPostDate()));
            postVH.tvDate.setText(date);
        }catch (Exception e){
            postVH.tvDate.setText("");
        }


    }

    @Override
    public int getItemCount() {
        return listTickets.size();
    }

    class PostVH extends RecyclerView.ViewHolder {
        ImageView imgUser, imgPost;
        TextView tvUserName, tvDate, tvPostText;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user);
            imgPost = itemView.findViewById(R.id.tweet_picture);
            tvUserName = itemView.findViewById(R.id.txtUserName);
            tvDate = itemView.findViewById(R.id.txt_tweet_date);
            tvPostText = itemView.findViewById(R.id.txt_tweet);
        }
    }
}
