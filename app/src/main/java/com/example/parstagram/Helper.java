package com.example.parstagram;

import android.content.Context;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;

public class Helper {

    public static void loadImage(Context context, ImageView imageView, ParseFile file) {
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .into(imageView);
        }
    }

    public static void loadCircleCropImage(Context context, ImageView imageView, ParseFile file) {
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .transform(new CircleCrop())
                    .into(imageView);
        }
    }
}
