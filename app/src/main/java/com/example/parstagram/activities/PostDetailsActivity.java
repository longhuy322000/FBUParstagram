package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.adapters.CommentsAdapter;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Like;
import com.example.parstagram.models.Post;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";
    private static final String KEY_EXTRA_USER = "user";
    private static final String KEY_USER_IMAGE = "image";
    private static final String KEY_EXTRA_POST_ID = "postId";

    TextView tvUsername;
    TextView tvDescription;
    ImageView ivImage;
    TextView tvLikes;
    ImageView ivUserCommentImage;
    ImageView ivUserImage;
    EditText etComment;
    Button btnSendComment;
    MaterialButton btnLike;
    RecyclerView rvComments;
    CommentsAdapter adapter;
    TextView tvRelativeTimestamp;

    Boolean liked;
    Post post;
    Like like;
    List<Comment> comments;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        tvLikes = findViewById(R.id.tvLikes);
        ivUserCommentImage = findViewById(R.id.ivUserCommentImage);
        ivUserImage = findViewById(R.id.ivUserImage);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnLike = findViewById(R.id.btnLike);
        tvRelativeTimestamp = findViewById(R.id.tvRelativeTimestamp);

        String postId = getIntent().getStringExtra(KEY_EXTRA_POST_ID);
        getPost(postId);

        // display user's image in comment section
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser getUser, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getUser", e);
                    return;
                }
                Helper.loadCircleCropImage(PostDetailsActivity.this, ivUserCommentImage, getUser.getParseFile(KEY_USER_IMAGE));
            }
        });

        // button like onClick
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLikeOnClick();
            }
        });
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendCommentOnClick();
            }
        });

        comments = new ArrayList<>();
        rvComments = findViewById(R.id.rvComments);
        adapter = new CommentsAdapter(PostDetailsActivity.this, comments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

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
        Intent intent = new Intent(PostDetailsActivity.this, UserProfileActivity.class);
        intent.putExtra(KEY_EXTRA_USER, user);
        startActivity(intent);
    }

    private void getPost(String objectId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECT_ID, objectId);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "error loading post");
                    return;
                }
                post = posts.get(0);
                bindData();
                checkLiked();
                queryComments();
            }
        });
    }

    private void bindData() {
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvRelativeTimestamp.setText(Post.getRelativeTimeAgo(post.getCreatedAt().toString()));

        // set likes TextView
        tvLikes.setText(post.getLikesString());

        // display post's image
        Helper.loadImage(PostDetailsActivity.this, ivImage, post.getImage());
        // display user's image
        Helper.loadCircleCropImage(PostDetailsActivity.this, ivUserImage, post.getUser().getParseFile(KEY_USER_IMAGE));
    }

    private void btnSendCommentOnClick() {
        final Comment comment = new Comment();
        comment.setComment(etComment.getText().toString());
        comment.setPost(post);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issues with creating new comment");
                    return;
                }
                etComment.setText("");
                Helper.hideKeyboard(PostDetailsActivity.this);
                comments.add(0, comment);
                adapter.notifyItemInserted(0);
                rvComments.smoothScrollToPosition(0);
            }
        });
    }

    private void btnLikeOnClick() {
        if (!liked) {
            like = post.incrementLike(ParseUser.getCurrentUser());
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "unable to saving post after incrementing", e);
                        return;
                    }
                    setLikeActive();
                    tvLikes.setText(post.getLikesString());
                    liked = true;
                }
            });
        }
        else {
            post.decrementLike(ParseUser.getCurrentUser(), like);
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "unable to saving post after decrementing", e);
                        return;
                    }
                    setLikeInactive();
                    tvLikes.setText(post.getLikesString());
                    liked = false;
                }
            });
        }
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.setLimit(20);
        query.addDescendingOrder(Comment.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> getComments, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Issue with getting comments", e);
                    return;
                }
                comments.addAll(getComments);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkLiked() {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST, post);
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

    public void setLikeActive() {
        btnLike.setIcon(getDrawable(R.drawable.ufi_heart_active));
        btnLike.setIconTintResource(R.color.red);
    }

    public void setLikeInactive() {
        btnLike.setIcon(getDrawable(R.drawable.ufi_heart));
        btnLike.setIconTintResource(R.color.black);
    }
}