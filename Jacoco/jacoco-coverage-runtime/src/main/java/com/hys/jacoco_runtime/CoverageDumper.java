package com.hys.jacoco_runtime;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class CoverageDumper {

    private static final String TAG = "Jacoco";

    private CoverageDumper() {
    }

    public static void dump(Context context) {
        try {
            Class<?> rtClass = Class.forName("org.jacoco.agent.rt.RT");
            Object agent = rtClass.getMethod("getAgent").invoke(null);

            if (agent == null) {
                Log.e(TAG, "Failed to get JaCoCo agent - agent is null");
                return;
            }

            String filePath = new File(context.getFilesDir(), "coverage.ec").getAbsolutePath();
            Log.i(TAG, "Dumping coverage to " + filePath);

            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                Log.w(TAG, "Failed to create directory for coverage file: " + parentDir.getAbsolutePath());
            }

            byte[] executionData = (byte[]) agent.getClass()
                    .getMethod("getExecutionData", boolean.class)
                    .invoke(agent, false);

            writeBytes(file, executionData);

            Log.i(TAG, "Coverage dumped to " + filePath);
        } catch (Exception e) {
            Log.e(TAG, "Dump failed", e);
        }
    }

    private static void writeBytes(File destination, byte[] data) throws IOException {
        try (OutputStream out = new FileOutputStream(destination, false)) {
            out.write(data);
            out.flush();
        }
    }
}

