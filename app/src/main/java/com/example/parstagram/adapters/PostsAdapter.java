package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.activities.UserProfileActivity;
import com.example.parstagram.models.Like;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.SavedPost;
import com.google.android.material.button.MaterialButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";
    private static final String KEY_EXTRA_POST_ID = "postId";
    private static final String KEY_EXTRA_USER = "user";
    private static final String KEY_USER_IMAGE = "image";

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

        Post post;
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivImage;
        ImageView ivUserImage;
        MaterialButton btnLike;
        MaterialButton btnSavePost;
        TextView tvRelativeTimestamp;

        Boolean liked;
        Like like;
        Boolean saved;
        SavedPost savedPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnSavePost = itemView.findViewById(R.id.btnSavePost);
            tvRelativeTimestamp = itemView.findViewById(R.id.tvRelativeTimestamp);
        }

        public void bind(final Post post) {
            this.post = post;
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvRelativeTimestamp.setText(Post.getRelativeTimeAgo(post.getCreatedAt().toString()));
            // load post's image
            Helper.loadImage(context, ivImage, post.getImage());
            // load user's image
            Helper.loadCircleCropImage(context, ivUserImage, post.getUser().getParseFile(KEY_USER_IMAGE));
            checkLiked();
            checkSavedPost();

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

            btnSavePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Post post = posts.get(getAdapterPosition());
                    if (!saved) {
                        savedPost = new SavedPost();
                        savedPost.setUser(ParseUser.getCurrentUser());
                        savedPost.setPost(post);
                        savedPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Issues with saving post");
                                    return;
                                }
                                saved = true;
                                setSaveActive();
                            }
                        });
                    }
                    else {
                        savedPost.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Issues with deleting savedPost");
                                    return;
                                }
                                saved = false;
                                savedPost = null;
                                setSaveInActive();
                            }
                        });
                    }
                }
            });

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToUserProfile(post.getUser());
                }
            });

            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToUserProfile(post.getUser());
                }
            });
        }

        private void goToUserProfile(ParseUser user) {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra(KEY_EXTRA_USER, user);
            context.startActivity(intent);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra(KEY_EXTRA_POST_ID, post.getObjectId());
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
                    }
                }
            });
        }

        private void checkSavedPost() {
            ParseQuery<SavedPost> query = ParseQuery.getQuery(SavedPost.class);
            query.whereEqualTo(SavedPost.KEY_POST, posts.get(getAdapterPosition()));
            query.whereEqualTo(SavedPost.KEY_USER, ParseUser.getCurrentUser());
            query.findInBackground(new FindCallback<SavedPost>() {
                @Override
                public void done(List<SavedPost> savedPosts, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issues with checking savedPost");
                        return;
                    }
                    if (savedPosts.isEmpty()) {
                        saved = false;
                        setSaveInActive();
                    }
                    else {
                        saved = true;
                        savedPost = savedPosts.get(0);
                        setSaveActive();
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

        public void setSaveActive() {
            btnSavePost.setIcon(context.getDrawable(R.drawable.ufi_save_active));
            btnSavePost.setIconTintResource(R.color.black);
        }

        public void setSaveInActive() {
            btnSavePost.setIcon(context.getDrawable(R.drawable.ufi_save));
            btnSavePost.setIconTintResource(R.color.black);
        }
    }
}
