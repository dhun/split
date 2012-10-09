package jp.gr.java_conf.dhun.split.ui.activity;

import jp.gr.java_conf.dhun.split.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    private Animation fadein;
    private Animation fadeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // XMLレイアウトのロード
        setContentView(R.layout.activity_splash);

        // ロゴ
        final ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

        // フェードイン
        fadein = AnimationUtils.loadAnimation(this, R.anim.anim_fadein);
        fadein.setDuration(3000);
        fadein.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(TAG, "onAnimationEnd. animation=[" + animation + "]");

                // 適当に待機してから、終了アニメーションをスタート
                logoImageView.startAnimation(fadeout);
            }
        });

        // フェードアウト
        fadeout = AnimationUtils.loadAnimation(this, R.anim.anim_fadeout);
        fadeout.setStartOffset(2000L);
        fadeout.setDuration(3000L);
        fadeout.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(TAG, "onAnimationEnd. animation=[" + animation + "]");

                // トップメニューを表示して、byebye.
                Intent intent = new Intent(SplashActivity.this, TopMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 開始アニメーションをスタート
        logoImageView.startAnimation(fadein);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");

        // 終了アニメーションの実行時間を短めに変更
        fadeout.setStartOffset(0);
        fadeout.setDuration(500L);

        // 開始アニメーションをキャンセル(終了アニメーションが開始される)
        fadein.cancel();

        return true;
    }

}
