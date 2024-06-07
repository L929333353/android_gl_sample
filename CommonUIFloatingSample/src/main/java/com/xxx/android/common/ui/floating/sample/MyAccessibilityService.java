package com.xxx.android.common.ui.floating.sample;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent");
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

}
