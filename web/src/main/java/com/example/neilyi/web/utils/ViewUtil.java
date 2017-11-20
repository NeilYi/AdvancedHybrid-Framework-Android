package com.example.neilyi.web.utils;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * ViewUtil
 *
 * Created by NeilYi on 2017/11/16.
 */
public class ViewUtil {

    public static void setCustomBackground(View view, int resId) {
        Drawable drawable = view.getResources().getDrawable(resId);
        setCustomBackground(view, drawable);
    }

    public static void setCustomBackground(View view, Drawable drawable){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}
