package com.xxx.android.common.ui.floating.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.xxx.android.common.ui.floating.FloatingWindow;
import com.xxx.android.common.ui.floating.sample.gl.glestextureview.DemoGlesTextureView;

import java.lang.ref.WeakReference;

public class FloatingWindowTestActivity extends AppCompatActivity {

    private static final String TAG = "FloatingWindowTest";

    private static final int REQ_CODE_DRAW_OVERLAY = 3;

    private FloatingWindow floatingWindow;

    private FloatingWindow.Mode mode = FloatingWindow.Mode.HOVER;

    ActivityResultLauncher<Integer> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    });
        }

        setContentView(R.layout.activity_floating_window_test);

        findViewById(R.id.btn_hover).setOnClickListener(v -> {
            mode = FloatingWindow.Mode.HOVER;
            ((EditText) findViewById(R.id.edit_mode)).setText("HOVER");
        });

        findViewById(R.id.btn_corner).setOnClickListener(v -> {
            mode = FloatingWindow.Mode.CORNER;
            ((EditText) findViewById(R.id.edit_mode)).setText("CORNER");
        });

        findViewById(R.id.btn_border).setOnClickListener(v -> {
            mode = FloatingWindow.Mode.BORDER;
            ((EditText) findViewById(R.id.edit_mode)).setText("BORDER");
        });

        findViewById(R.id.btn_border_hori).setOnClickListener(v -> {
            mode = FloatingWindow.Mode.BORDER_HORIZONTAL;
            ((EditText) findViewById(R.id.edit_mode)).setText("BORDER HORIZONTAL");
        });

        ((Switch) findViewById(R.id.switch_interact_with_im)).setOnCheckedChangeListener((v, checked) -> {
            if (checked) {
                ((Switch) findViewById(R.id.switch_cover_bars)).setChecked(false);
            }
        });

        ((Switch) findViewById(R.id.switch_cover_bars)).setOnCheckedChangeListener((v, checked) -> {
            if (checked) {
                ((Switch) findViewById(R.id.switch_interact_with_im)).setChecked(false);
            }
        });

        findViewById(R.id.btn_show).setOnClickListener(v -> {
            if (((Switch) findViewById(R.id.switch_global)).isChecked() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FloatingWindowTestActivity.this);
                builder.setTitle("若使用此功能，需要为App开启悬浮框权限");
                builder.setNegativeButton("去授权", (dialog, which) -> {
                    resultLauncher.launch(REQ_CODE_DRAW_OVERLAY);
                });
                builder.setPositiveButton("取消", (dialog, which) -> {

                });
                builder.create().show();
            }

            if (floatingWindow == null) {
                floatingWindow = new FloatingWindow(FloatingWindowTestActivity.this,
                        Integer.parseInt(((EditText) findViewById(R.id.edit_width)).getText().toString()),
                        Integer.parseInt(((EditText) findViewById(R.id.edit_height)).getText().toString())
                );
            }

            View view = getLayoutInflater().inflate(R.layout.window_floating_test, null);
            DemoGlesTextureView textureView = view.findViewById(R.id.texture_view);
            final WeakReference<DemoGlesTextureView> weakReference = new WeakReference<>(textureView);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        if (weakReference.get() == null) {
                            return;
                        }
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        weakReference.get().requestRender();
                    }
                }
            }).start();

            view.findViewById(R.id.btn_dismiss).setOnClickListener(v1 -> {
                floatingWindow.destroy();
                floatingWindow = null;
            });

//            LottieAnimationView lottie = view.findViewById(R.id.lottie_1);
//            lottie.setAnimation("game/game0.json");
//            lottie.setImageAssetsFolder("images/");
//            lottie.setRepeatCount(LottieDrawable.INFINITE);
//            lottie.playAnimation();

            floatingWindow.setContentView(view);

            floatingWindow.x = Integer.parseInt(((EditText) findViewById(R.id.edit_x)).getText().toString());
            floatingWindow.y = Integer.parseInt(((EditText) findViewById(R.id.edit_y)).getText().toString());
            floatingWindow.width = Integer.parseInt(((EditText) findViewById(R.id.edit_width)).getText().toString());
            floatingWindow.height = Integer.parseInt(((EditText) findViewById(R.id.edit_height)).getText().toString());

            EditText editLeft = findViewById(R.id.edit_bounding_left);
            EditText editTop = findViewById(R.id.edit_bounding_top);
            EditText editRight = findViewById(R.id.edit_bounding_right);
            EditText editBottom = findViewById(R.id.edit_bounding_bottom);

            floatingWindow.boundaryInsets(Integer.parseInt(editLeft.getText().toString()),
                    Integer.parseInt(editTop.getText().toString()),
                    Integer.parseInt(editRight.getText().toString()),
                    Integer.parseInt(editBottom.getText().toString())
            );

            floatingWindow.scope(((Switch) findViewById(R.id.switch_global)).isChecked() ? FloatingWindow.Scope.GLOBAL : FloatingWindow.Scope.ACTIVITY)
                    .interactWithIM(((Switch) findViewById(R.id.switch_interact_with_im)).isChecked())
                    .coverBars(((Switch) findViewById(R.id.switch_cover_bars)).isChecked())
                    .mode(mode)
                    .show();
        });

        findViewById(R.id.btn_update).setOnClickListener(v -> {
            if (floatingWindow != null) {
                floatingWindow.x = -500;
                floatingWindow.update();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (floatingWindow != null && !floatingWindow.isShowing() && !((Switch) findViewById(R.id.switch_global)).isChecked()) {
            floatingWindow.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatingWindow != null && floatingWindow.isShowing() && !((Switch) findViewById(R.id.switch_global)).isChecked()) {
            floatingWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultLauncher != null) {
            resultLauncher.unregister();
        }
        if (floatingWindow != null && !((Switch) findViewById(R.id.switch_global)).isChecked()) {
            floatingWindow.destroy();
            floatingWindow = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e(TAG, "========finalize=======");
    }

    public static void loadLottieFromAsset(Context context, final LottieAnimationView lottie, String name, final boolean isPlay, final boolean loop) {
        try {
            LottieTask<LottieComposition> lottieCompositionLottieTask = LottieCompositionFactory.fromAsset(context, name);
            lottieCompositionLottieTask.addListener(new LottieListener<LottieComposition>() {
                @Override
                public void onResult(LottieComposition result) {
                    if (result != null) {
                        try {
                            lottie.setComposition(result);
                            if (isPlay) {
                                lottie.playAnimation();
                            }
                            lottie.loop(loop);
                        } catch (Throwable e) {
                            e.getMessage();
                        }
                    }
                }
            }).addFailureListener(new LottieListener<Throwable>() {
                @Override
                public void onResult(Throwable result) {
                    result.printStackTrace();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}