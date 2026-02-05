package com.example.ubercorp.utils;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

public class AppForegroundChecker {

    public static boolean isAppInForeground(Context context) {
        return ProcessLifecycleOwner.get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }
}