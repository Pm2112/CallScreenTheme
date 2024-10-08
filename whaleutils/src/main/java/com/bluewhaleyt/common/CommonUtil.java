package com.bluewhaleyt.common;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.elevation.SurfaceColors;

import java.util.concurrent.Callable;

public class CommonUtil {

    public static final int SURFACE_FOLLOW_DEFAULT_TOOLBAR = 0;
    public static final int SURFACE_FOLLOW_WINDOW_BACKGROUND = 1;

    public static void waitForTimeThenDo(double time, Callable<Void> callable) {
        Handler handler = new Handler();
        handler.postDelayed((Runnable) () -> {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, (long) time);
    }

    public static void repeatDoing(Callable<?> callable) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    callable.call();
                    handler.postDelayed(this, 1L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(runnable);
    }

    public static boolean isInDarkMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;

            case Configuration.UI_MODE_NIGHT_NO:
                return false;
        }
        return true;
    }

    public static void setStatusBarColorWithSurface(AppCompatActivity activity, int type) {
        switch (type) {
            case SURFACE_FOLLOW_DEFAULT_TOOLBAR:
                activity.getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(activity));
                break;
            case SURFACE_FOLLOW_WINDOW_BACKGROUND:
                activity.getWindow().setStatusBarColor(SurfaceColors.SURFACE_5.getColor(activity));
                break;
        }
    }

    public static void setNavigationBarColorWithSurface(AppCompatActivity activity, int type) {
        switch (type) {
            case SURFACE_FOLLOW_WINDOW_BACKGROUND:
                activity.getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(activity));
                break;
        }
    }

    public static void setToolBarColorWithSurface(AppCompatActivity activity, int type) {
        switch (type) {
            case SURFACE_FOLLOW_WINDOW_BACKGROUND:
                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(type));
                break;
        }
    }

}
