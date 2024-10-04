package com.bluewhaleyt.crashdebugger;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private String lineBreak = "\n";
    private StringBuilder strBuilderMessage = new StringBuilder();
    private StringBuilder strBuilderSoftwareInfo = new StringBuilder();
    private StringBuilder strBuilderDateInfo = new StringBuilder();
    private Context context;

    public CrashHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {

        var stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));

        strBuilderMessage.append(stackTrace);

        strBuilderSoftwareInfo.append(context.getResources().getString(R.string.sdk_version) + ": ");
        strBuilderSoftwareInfo.append(Build.VERSION.SDK_INT);
        strBuilderSoftwareInfo.append(lineBreak);
        strBuilderSoftwareInfo.append(context.getResources().getString(R.string.android_version) + ": ");
        strBuilderSoftwareInfo.append(Build.VERSION.RELEASE);
        strBuilderSoftwareInfo.append(lineBreak);
        strBuilderSoftwareInfo.append(context.getResources().getString(R.string.incremental) + ": ");
        strBuilderSoftwareInfo.append(Build.VERSION.INCREMENTAL);
        strBuilderSoftwareInfo.append(lineBreak);

        strBuilderDateInfo.append(Calendar.getInstance().getTime());
        strBuilderDateInfo.append(lineBreak);

        Log.d("error", strBuilderMessage.toString());
        Log.d("software", strBuilderSoftwareInfo.toString());
        Log.d("date", strBuilderDateInfo.toString());

        var intent = new Intent(context, DebugActivity.class);
        intent.putExtra("error", strBuilderMessage.toString());
        intent.putExtra("software", strBuilderSoftwareInfo.toString());
        intent.putExtra("date", strBuilderDateInfo.toString());

        context.startActivity(intent);

        Process.killProcess(Process.myPid());
        System.exit(2);

    }
}
