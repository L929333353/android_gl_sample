package com.xxx.android.common.ui.floating.sample;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xxx.android.foundation.Activities;

public class MyLayout extends LinearLayout {

    private OnTouchMovedListener mOnTouchMovedListener;

    public void setOnTouchMovedListener(OnTouchMovedListener listener) {
        this.mOnTouchMovedListener = listener;
    }

    public MyLayout(Context context) {
        super(context);
    }

    public MyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            Activity topActivity = Activities.getTopActivity();
//            if (topActivity != null) {
//                Activities.getTopActivity().onBackPressed();
//            }
//            return false;
//        }
        return true;
    }

    private int x;
    private int y;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);

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
                if (mOnTouchMovedListener != null) {
                    mOnTouchMovedListener.onTouchMoved(movedX, movedY);
                }
                break;
            default:
                break;
        }

        return true;
    }

    public static interface OnTouchMovedListener {
        void onTouchMoved(int x, int y);
    }
}
