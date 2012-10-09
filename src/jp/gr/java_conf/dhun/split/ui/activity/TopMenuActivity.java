package jp.gr.java_conf.dhun.split.ui.activity;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import jp.gr.java_conf.dhun.split.manager.GameMode;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TopMenuActivity extends Activity {
    private static final String TAG = TopMenuActivity.class.getSimpleName();

    private GameConfig config;
    private MediaPlayer bgmPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top_menu);

        // ノーマルモード
        ((TextView) findViewById(R.id.lbl_normal_mode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopMenuActivity.this, GameMainActivity.class);
                intent.putExtra(GameMode.TAG, GameMode.NORMAL_MODE.name());
                startActivity(intent);
            }
        });

        // エンドレスモード
        ((TextView) findViewById(R.id.lbl_endress_mode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopMenuActivity.this, GameMainActivity.class);
                intent.putExtra(GameMode.TAG, GameMode.ENDRESS_MODE.name());
                startActivity(intent);
            }
        });

        // これまでの成績
        ((TextView) findViewById(R.id.lbl_game_record)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopMenuActivity.this, GameRecordActivity.class);
                startActivity(intent);
            }
        });

        // オプション設定
        ((TextView) findViewById(R.id.lbl_option)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopMenuActivity.this, GameConfigActivity.class);
                startActivity(intent);
            }
        });

        bgmPlayer = MediaPlayer.create(this, R.raw.bgm_title);
        bgmPlayer.setLooping(true);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // ゲーム設定のロード
        config = new GameConfig();
        config.load(this);

        if (config.isSoundBgm()) {
            bgmPlayer.seekTo(0);
            bgmPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        if (config.isSoundBgm()) {
            bgmPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (config.isSoundBgm()) {
            bgmPlayer.stop();
        }
        bgmPlayer.release();
    }
}
