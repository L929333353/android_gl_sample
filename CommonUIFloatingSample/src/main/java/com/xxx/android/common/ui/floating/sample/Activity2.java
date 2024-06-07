package com.xxx.android.common.ui.floating.sample;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Activity2 extends AppCompatActivity {

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("窗口2");
        setContentView(R.layout.activity_2);

        findViewById(R.id.btn_dialog).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity2.this);
            builder.setTitle("我是对话框");
            builder.setPositiveButton("OK", (dialog, witch) -> {
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, witch) -> {
                dialog.dismiss();
            });
            builder.create().show();
        });

        findViewById(R.id.btn_popup).setOnClickListener(v -> {
            if (popupWindow != null) {
                return;
            }

            View view = getLayoutInflater().inflate(R.layout.popup, null);
            view.findViewById(R.id.btn_dismiss).setOnClickListener(v1 -> {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });
            popupWindow = new PopupWindow(Activity2.this);
            popupWindow.setContentView(view);
            popupWindow.setWidth(500);
            popupWindow.setHeight(500);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                popupWindow.showAtLocation(findViewById(R.id.btn_popup), Gravity.CENTER, 0, 0);
            } else {
                popupWindow.showAsDropDown(getWindow().getDecorView(), 220, 220);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}