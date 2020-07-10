package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.models.Post;

import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    public static final String KEY_POST_ID = "postId";

    Context context;
    List<Post> posts;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImagePost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImagePost = itemView.findViewById(R.id.ivImagePost);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            Helper.loadImage(context, ivImagePost, post.getImage());
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra(KEY_POST_ID, posts.get(getAdapterPosition()).getObjectId());
            context.startActivity(intent);
        }
    }
}
