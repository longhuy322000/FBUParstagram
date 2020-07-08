package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";

    ParseUser currentUser;
    TextView tvUsername;
    TextView tvDescription;
    ImageView ivImage;
    TextView tvLikes;
    TextView tvTimestamp;
    ImageView ivUserCommentImage;
    ImageView ivUserImage;
    EditText etComment;
    Button btnSendComment;
    MaterialButton btnLike;

    Boolean liked;
    Post post;
    Like like;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        tvLikes = findViewById(R.id.tvLikes);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivUserCommentImage = findViewById(R.id.ivUserCommentImage);
        ivUserImage = findViewById(R.id.ivUserImage);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnLike = findViewById(R.id.btnLike);

        post = getIntent().getParcelableExtra("post");
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        // set likes TextView
        tvLikes.setText(post.getLikesString());

        // display post's image
        Helper.loadImage(PostDetailsActivity.this, ivImage, post.getImage());
        // display user's image
        Helper.loadCircleCropImage(PostDetailsActivity.this, ivUserImage, post.getUser().getParseFile("image"));

        // display user's image in comment section
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser getUser, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getUser", e);
                    return;
                }
                currentUser = getUser;
                Helper.loadCircleCropImage(PostDetailsActivity.this, ivUserCommentImage, currentUser.getParseFile("image"));
            }
        });

        // button like onClick
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            tvLikes.setText(post.getLikesString());
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
                            tvLikes.setText(post.getLikesString());
                            liked = false;
                        }
                    });
                }
            }
        });

        checkLiked();
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