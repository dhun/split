package jp.gr.java_conf.dhun.split.ui.activity;

import jp.gr.java_conf.dhun.split.R;

import jp.gr.java_conf.dhun.split.manager.GameConfig;
import jp.gr.java_conf.dhun.split.manager.GameConfig.BallFaceType;
import jp.gr.java_conf.dhun.split.manager.GameManager;
import jp.gr.java_conf.dhun.split.manager.GameMode;
import jp.gr.java_conf.dhun.split.manager.info.PlayRecord;
import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;
import jp.gr.java_conf.dhun.split.ui.dialog.NameEntryDialog;
import android.app.Activity;
import android.app.ActivityResultDispatcher;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

public class GameMainActivity extends Activity {
    private static final String TAG = GameMainActivity.class.getSimpleName();

    private GameManager gameManager;
    private ActivityResultDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        // アクティビティ間の処理結果を割り当てるディスパッチャを生成
        dispatcher = new ActivityResultDispatcher(this);

        // ハイスコア更新時のコールバック処理
        // ※UI操作をメインスレッド側で実行させるための実装だが、パラメータの渡し方がなんか気に入らない
        Handler callbackHandler = new Handler();
        Runnable callbackCommand = new Runnable() {
            @Override
            public void run() {
                GameConfig config = gameManager.getGameConfig();
                GameRecord highScore = gameManager.getHighScore();
                if (!config.isShowNameEntryDialog()) {
                    // 前回エントリされた名前で自動保存. ダイアログは表示しない
                    highScore.setEntryName(config.getLastEntryName());
                    highScore.store(GameMainActivity.this);
                    return;
                }

                // ネームエントリ画面を表示
                showDialog(0);
            }
        };

        // ゲーム設定のロード
        GameConfig config = new GameConfig();
        config.load(this);

        // ボール表面とクリップしたボール画像との相関チェック
        if (BallFaceType.IMAGE_CLIP == config.getBallFaceType() && config.getBallImages().isEmpty()) {
            config.setBallFaceType(BallFaceType.RANDOM_COLOR);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.error_config_ballImagesEmpty));
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        // インテントからゲームモードを取得
        Intent intent = getIntent();
        GameMode gameMode = GameMode.valueOf(intent.getStringExtra(GameMode.TAG));

        // ゲームマネージャの準備
        gameManager = new GameManager(this, config, gameMode);
        gameManager.setOnUpdateHighscoreCommand(callbackHandler, callbackCommand);

        // サーフェースビューの準備
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(gameManager);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    gameManager.touch(event.getX(), event.getY());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        gameManager.pause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        gameManager.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        menu.add(0, 0, 0, R.string.game_menu_restart);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        gameManager.pause();
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.d(TAG, "onMenuItemSelected. featureId=[" + featureId + "]");

        switch (item.getItemId()) {
        case 0:
            // 再開
            gameManager.unpause();
            break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // ネームエントリ画面を表示
        GameConfig config = gameManager.getGameConfig();
        PlayRecord highScore = gameManager.getHighScore();
        NameEntryDialog dialog = new NameEntryDialog(GameMainActivity.this, config, highScore);
        dialog.setActivityResultDispatcher(dispatcher);
        return dialog;
    }

    /**
     * 音声認識アクティビティからのお返事をディスパッチ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dispatcher.dispatchActivityResult(requestCode, resultCode, data);
    }
}
