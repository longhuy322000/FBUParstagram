package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parstagram.CameraHelper;
import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.adapters.ProfilePostsAdapter;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final String KEY_USER_IMAGE = "image";
    private static final String KEY_EXTRA_USER = "user";

    ParseUser user;
    TextView tvUsername;
    ImageView ivUserImage;
    RecyclerView rvPosts;
    ProfilePostsAdapter adapter;
    List<Post> allPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = getIntent().getParcelableExtra(KEY_EXTRA_USER);

        rvPosts = findViewById(R.id.rvPosts);
        tvUsername = findViewById(R.id.tvUsername);
        ivUserImage = findViewById(R.id.ivUserImage);

        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(this, allPosts);
        rvPosts.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvPosts.setLayoutManager(gridLayoutManager);

        queryPosts();

        tvUsername.setText(user.getUsername());
        Helper.loadCircleCropImage(UserProfileActivity.this, ivUserImage, user.getParseFile(KEY_USER_IMAGE));
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Issue with getting posts", e);
                    return;
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}