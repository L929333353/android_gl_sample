package com.xxx.android.common.ui.floating.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xxx.android.common.ui.floating.FloatingWindow;
import com.xxx.android.common.ui.floating.OnActivityChangeListener;
import com.xxx.android.foundation.Activities;
import com.xxx.android.foundation.Apps;
import com.xxx.android.foundation.Foundations;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQ_CODE_DRAW_OVERLAY = 3;

    private WindowManager windowManager;

    private WindowManager.LayoutParams layoutParams;

    private boolean showing;

    private boolean needShow;

    ActivityResultLauncher<Integer> resultLauncher;

    int wid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("窗口1");
        setContentView(R.layout.activity_main);

        Activities.addActivityLifecycleCallbacks(new Foundations.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                super.onActivityStopped(activity);
                if (!Apps.isAppForeground()) {
                    unBubble();
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                if (needShow) {
                    bubble();
                }
            }
        });

        if (resultLauncher == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resultLauncher = registerForActivityResult(
                    new ActivityResultContract<Integer, Integer>() {
                        @NonNull
                        @Override
                        public Intent createIntent(@NonNull Context context, Integer input) {
                            return new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        }

                        @Override
                        public Integer parseResult(int resultCode, @Nullable Intent intent) {
                            return resultCode;
                        }
                    },
                    result -> {
                        if (result == 0) {
                            bubble();
                        }
                    });
        }

        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.packageName = getApplication().getPackageName();
            layoutParams.width = 300;
            layoutParams.height = 300;

            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 外围可点击
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 不阻碍按键事件和键盘弹出
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM // 跟键盘交互，不会覆盖键盘
//                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR // 两者配合，可以拖到状态栏
//                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            ;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
//            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;

            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.x = 0;
            layoutParams.y = 0;
        }

        findViewById(R.id.btn_bubble).setOnClickListener(v -> {
            needShow = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                bubble();
            } else {
//            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQ_CODE_DRAW_OVERLAY);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("若使用此功能，需要为App开启悬浮框权限");
                builder.setNegativeButton("去授权", (dialog, which) -> {
                    resultLauncher.launch(REQ_CODE_DRAW_OVERLAY);
                });
                builder.setPositiveButton("取消", (dialog, which) -> {

                });
                builder.create().show();
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Activity2.class));
        });

        findViewById(R.id.btn_service).setOnClickListener(v -> {
            startService(new Intent(MainActivity.this, MyService.class));
        });

        findViewById(R.id.btn_floating_window_test).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FloatingWindowTestActivity.class));
        });

        findViewById(R.id.btn_floating_window_test_cycle).setOnClickListener(v -> {
            View view = createFloatingWindowContentView(MainActivity.this);
//            wid = FloatingWindow.create(MainActivity.this, view, 300, 300, FloatingWindow.Mode.HOVER, 0, 0, new OnActivityChangeListener() {
//                @Override
//                public View onActivityChanged(FloatingWindow floatingWindow, Activity newActivity) {
////                    return createFloatingWindowContentView(newActivity);
//                    return null;
//                }
//            }, false, false);
            wid = FloatingWindow.create(MainActivity.this, view, 300, 300, FloatingWindow.Mode.HOVER, 0, 0, null, false, false);
            FloatingWindow.show(wid);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FloatingWindow.destroy(wid);
    }

    private View createFloatingWindowContentView(Activity activity) {
        View view = activity.getLayoutInflater().inflate(R.layout.window_floating_test, null);
        view.findViewById(R.id.btn_dismiss).setOnClickListener(v1 -> {
            FloatingWindow.destroy(wid);
        });

        return view;
    }

    View view;

    private void bubble() {
        if (showing) {
            return;
        }

        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.floating, null);
            ((MyLayout) view).setOnTouchMovedListener((x, y) -> {
                layoutParams.x = layoutParams.x + x;
                layoutParams.y = layoutParams.y + y;

                // 更新悬浮窗控件布局
                windowManager.updateViewLayout(view, layoutParams);
            });
            view.findViewById(R.id.btn_dismiss).setOnClickListener(v1 -> {
                needShow = false;
                unBubble();
            });
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                try {
                    windowManager.addView(view, layoutParams);
                    showing = true;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void unBubble() {
        if (showing) {
            windowManager.removeView(view);
            showing = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_DRAW_OVERLAY && resultCode == 0) {
            bubble();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultLauncher != null) {
            resultLauncher.unregister();
        }
        windowManager = null;
        layoutParams = null;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unBubble();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (needShow) {
//            bubble();
//        }
//    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}