package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.models.Like;
import com.example.parstagram.models.Post;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";

    Context context;
    List<Post> posts;
    ParseUser currentUser;

    public PostsAdapter(Context context, List<Post> allPosts) {
        this.context = context;
        this.posts = allPosts;
        currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        private static final int DETAILS_REQUEST_CODE = 20;

        TextView tvUsername;
        TextView tvDescription;
        ImageView ivImage;
        ImageView ivUserImage;
        MaterialButton btnLike;
        Boolean liked;
        Like like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            btnLike = itemView.findViewById(R.id.btnLike);
        }

        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            // load post's image
            Helper.loadImage(context, ivImage, post.getImage());
            // load user's image
            Helper.loadCircleCropImage(context, ivUserImage, post.getUser().getParseFile("image"));
            checkLiked();

            // button like onClick
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Post post = posts.get(getAdapterPosition());
                    if (!liked) {
                        like = post.incrementLike(currentUser);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "unable to saving post after incrementing", e);
                                    return;
                                }
                                setLikeActive();
                                liked = true;
                            }
                        });
                    }
                    else {
                        post.decrementLike(currentUser, like);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "unable to saving post after decrementing", e);
                                    return;
                                }
                                setLikeInactive();
                                liked = false;
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("post", posts.get(getAdapterPosition()));
            context.startActivity(intent);
        }

        private void checkLiked() {
            ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
            query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(Like.KEY_POST, posts.get(getAdapterPosition()));
            query.findInBackground(new FindCallback<Like>() {
                @Override
                public void done(List<Like> likes, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "issues with getting like", e);
                        return;
                    }
                    if (likes.isEmpty()) {
                        liked = false;
                        setLikeInactive();
                    }
                    else {
                        liked = true;
                        like = likes.get(0);
                        setLikeActive();
                        Log.i(TAG, posts.get(getAdapterPosition()).getDescription());
                    }
                }
            });
        }

        public void setLikeActive() {
            btnLike.setIcon(context.getDrawable(R.drawable.ufi_heart_active));
            btnLike.setIconTintResource(R.color.red);
        }

        public void setLikeInactive() {
            btnLike.setIcon(context.getDrawable(R.drawable.ufi_heart));
            btnLike.setIconTintResource(R.color.black);
        }
    }
}
