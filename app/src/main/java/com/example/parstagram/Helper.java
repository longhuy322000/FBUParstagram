package com.example.parstagram;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Log.i("asdf", "hide keyboard");
    }
}
