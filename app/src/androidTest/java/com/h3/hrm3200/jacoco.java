package com.h3.hrm3200;

import android.util.*;

import java.lang.reflect.Method;

/**
 * Created by moonh on 2017-08-27.
 */

public class jacoco {
    static void generateCoverageReport() {
        String TAG = "jacoco";
        // use reflection to call emma dump coverage method, to avoid
        // always statically compiling against emma jar
        String coverageFilePath = "/storage/sdcard/coverage.exec";
        java.io.File coverageFile = new java.io.File(coverageFilePath);
        try {
            Class<?> emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
            Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData",
                    coverageFile.getClass(), boolean.class, boolean.class);

            dumpCoverageMethod.invoke(null, coverageFile, true, false);
            android.util.Log.e(TAG, "generateCoverageReport: ok");
        } catch (Exception  e) {
            e.printStackTrace();
//            throw new Throwable("Is emma jar on classpath?", e);
        }
    }
}
