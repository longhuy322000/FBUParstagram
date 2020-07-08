package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.CameraHelper;
import com.example.parstagram.Helper;
import com.example.parstagram.activities.LoginActivity;
import com.example.parstagram.R;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.adapters.ProfilePostsAdapter;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    CameraHelper cameraHelper;
    ParseUser user;
    TextView tvUsername;
    Button btnLogout;
    ImageView ivUserImage;
    RecyclerView rvPosts;
    ProfilePostsAdapter adapter;
    List<Post> allPosts;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraHelper = new CameraHelper(getContext(), this);
        rvPosts = view.findViewById(R.id.rvPosts);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivUserImage = view.findViewById(R.id.ivUserImage);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Issues with logout", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Issues with logout", e);
                        }
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        });

        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvPosts.setLayoutManager(gridLayoutManager);

        queryPosts();

        user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        // load user data in background
        user.fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting user", e);
                    return;
                }
                Helper.loadImage(getContext(), ivUserImage, user.getParseFile("image"));
            }
        });

        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.launchCamera();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                final Bitmap takenImage = BitmapFactory.decodeFile(cameraHelper.photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                user.put("image", new ParseFile(cameraHelper.photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ivUserImage.setImageBitmap(takenImage);
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
