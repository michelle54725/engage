package com.mao.engage.teacherclassactivity;
import android.graphics.Bitmap;
import android.view.View;

public class Screenshot {

    public static Bitmap takeScreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View v) {
        return takeScreenshot(v.getRootView());
    }

}