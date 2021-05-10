package com.hllbr.instaclonefirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.PostHoler> {
    private ArrayList<String> userEmailList;
    private ArrayList<String> userCommnentList ;
    private ArrayList<String> userImageList ;

    public FeedRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> userCommnentList, ArrayList<String> userImageList) {
        this.userEmailList = userEmailList;
        this.userCommnentList = userCommnentList;
        this.userImageList = userImageList;
    }

    @NonNull
    @Override
    public PostHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row,parent,false);

        return new PostHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHoler holder, int position) {
        holder.useremailText.setText(userEmailList.get(position));
        holder.commentText.setText(userCommnentList.get(position));
        //userImageList.get(position)
        Picasso.get().load(userImageList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {//kaç row var onu yazıyorum

        return userEmailList.size();
    }//başka bir yerden inherantance alıyorum


    //PostHolder = VH = GÖRÜNÜM TUTUCU olarak ifade edebilirim şuan yok bunu oluşturmama gerekicek
    class PostHoler extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView useremailText,commentText;

        public PostHoler(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerview_row_imageview);
            useremailText = itemView.findViewById(R.id.recyclerview_row_useremail_text);
            commentText = itemView.findViewById(R.id.recyclerview_row_comment_text);

        }
    }
}
