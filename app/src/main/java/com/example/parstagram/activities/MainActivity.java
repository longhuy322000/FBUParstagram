package com.example.parstagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.parstagram.R;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.instagram_home_outline_24);
                bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.instagram_new_post_outline_24);
                bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.instagram_user_outline_24);
                Fragment fragment = new Fragment();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        item.setIcon(R.drawable.instagram_home_filled_24);
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        item.setIcon(R.drawable.instagram_new_post_filled_24);
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        item.setIcon(R.drawable.instagram_user_filled_24);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}