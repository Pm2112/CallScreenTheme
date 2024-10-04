package com.bluewhaleyt.crashdebugger;

import android.content.Context;

public class CrashDebugger {

    public static void init(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context));
    }

}
