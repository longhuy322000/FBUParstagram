package com.example.parstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("SavedPost")
public class SavedPost extends ParseObject {

    public static final String KEY_POST = "post";
    public static final String KEY_USER = "user";

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static List<Post> fromSavedPostsArray(List<SavedPost> savedPosts) {
        List<Post> posts = new ArrayList<>();
        for (int i=0; i<savedPosts.size(); i++) {
            posts.add(savedPosts.get(i).getPost());
        }
        return posts;
    }
}
