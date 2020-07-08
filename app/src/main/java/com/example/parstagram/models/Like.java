package com.example.parstagram.models;

import com.example.parstagram.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }
}
