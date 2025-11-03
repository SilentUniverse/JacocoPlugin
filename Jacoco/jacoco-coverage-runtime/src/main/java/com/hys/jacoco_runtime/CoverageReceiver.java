package com.hys.jacoco_runtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CoverageReceiver extends BroadcastReceiver {

    private static final String TAG = "Jacoco";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Coverage dump start");
        CoverageDumper.dump(context);
        Log.i(TAG, "Coverage dump end");
    }
}

