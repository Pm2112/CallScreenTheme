package com.example.callscreenapp.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.UUID;

public class Constants {
    public static final String THEME_DIR = "textmate/themes/";
    public static final String LANGUAGE_DIR = "textmate/languages/";

    public static final String CODE_FONT = "fonts/JetBrainsMono-Medium.ttf";
    public static String productIdBuy = "";

    public static boolean USE_FAKE_SERVER = true;
    public static String PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions";
    public static String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?product=%s&package=%s";

//    public static void ratePopupApp(Activity activity){
//        ReviewManager manager =
//                ReviewManagerFactory.create(activity);
////                new FakeReviewManager(activity);
//
//        Task<ReviewInfo> request = manager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            try {
//                if (task.isSuccessful()) {
//                    // We can get the ReviewInfo object
//                    ReviewInfo reviewInfo = task.getResult();
//                    Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
//                    flow.addOnCompleteListener(task2 -> {
//                        // The flow has finished. The API does not indicate whether the user
//                        // reviewed or not, or even whether the review dialog was shown. Thus, no
//                        // matter the result, we continue our app flow.
//
//                    });
//                } else {
//                    // There was some problem, continue regardless of the result.
//
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }

    public static void rateApp(Activity activity)
    {
        try
        {
            Intent rateIntent = rateIntentForUrl(activity,"https://play.google.com/store/apps/details");
            activity.startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl(activity,"market://details");
            activity.startActivity(rateIntent);
        }
    }

    private static Intent rateIntentForUrl(Activity activity, String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, activity.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        intent.addFlags(flags);
        return intent;
    }

    @Nullable
    public static String getUUId() {
        UUID wideVineUuid = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
        MediaDrm wvDrm = null;
        try {
            wvDrm = new MediaDrm(wideVineUuid);
            byte[] wideVineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
            //optional encoding to convert the array in string.
            return Base64.encodeToString(wideVineId, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (wvDrm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    wvDrm.close();
                } else {
                    wvDrm.release();
                }
            }
        }
    }
}
