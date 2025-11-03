package com.hys.jacoco_runtime;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

public final class CoverageHelper {

    private static final String ACTION_DUMP = "JACOCO_DUMP";
    private static CoverageReceiver receiver;

    private CoverageHelper() {
    }

    public static void initCoverageDump(Context context) {
        if (receiver != null) {
            return;
        }

        receiver = new CoverageReceiver();
        IntentFilter filter = new IntentFilter(ACTION_DUMP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(receiver, filter);
        }
    }

    public static void unregisterCoverageDump(Context context) {
        if (receiver == null) {
            return;
        }
        context.unregisterReceiver(receiver);
        receiver = null;
    }
}

