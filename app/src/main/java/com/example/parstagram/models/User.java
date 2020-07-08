package com.example.parstagram.models;

import com.parse.ParseFile;
import com.parse.ParseUser;

public class User extends ParseUser {

    public static final String KEY_IMAGE = "image";

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }
}
