package jp.gr.java_conf.dhun.split.manager;

import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jp.gr.java_conf.dhun.split.character.BackGround;
import jp.gr.java_conf.dhun.split.character.GameInformationArea;
import jp.gr.java_conf.dhun.split.character.ball.IBall;
import jp.gr.java_conf.dhun.split.character.usertouch.UserTouch;
import jp.gr.java_conf.dhun.split.manager.ball.BallManager;
import jp.gr.java_conf.dhun.split.manager.info.GameInformation;
import jp.gr.java_conf.dhun.split.manager.info.GameStates;
import jp.gr.java_conf.dhun.split.manager.info.GameSubState;
import jp.gr.java_conf.dhun.split.manager.info.PlayRecord;
import jp.gr.java_conf.dhun.split.manager.sound.SoundController;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameManager implements SurfaceHolder.Callback {
    public static final long FPS = 60;

    private static final String TAG = GameManager.class.getSimpleName();
    private static final int PLAY_TIME_SEC = 30;

    private final Context context;
    private final GameConfig config;

    private final GameInformation info;
    private final BallManager ballManager;
    private final SoundController soundController;
    private final UserTouch userTouch;

    private SurfaceHolder surfaceHolder;
    private Rect gameRect; // ゲーム領域
    private Rect infoRect; // 情報領域

    private BackGround backGround;
    private GameInformationArea infoArea;

    private ScheduledExecutorService executor;
    private long endMills;
    private long pauseMills;
    private boolean processing;

    private Handler foregroundHandler;
    private Runnable callbackCommand;

    public GameManager(Context context, GameConfig config, GameMode gameMode) {
        this.context = context;
        this.config = config;

        info = new GameInformation(config, gameMode);

        ballManager = new BallManager(context, config);
        soundController = new SoundController(context, config);

        userTouch = new UserTouch();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated. isCreating=" + surfaceHolder.isCreating());
        if (null != this.surfaceHolder) {
            // 初回以外は無視
            return;
        }
        this.surfaceHolder = surfaceHolder;

        // 画面を２つの領域に分割
        gameRect = new Rect();
        gameRect.left = 0;
        gameRect.top = 0;
        gameRect.right = surfaceHolder.getSurfaceFrame().width();
        gameRect.bottom = (int) (surfaceHolder.getSurfaceFrame().height() * 0.85);

        infoRect = new Rect();
        infoRect.left = 0;
        infoRect.top = gameRect.height();
        infoRect.right = gameRect.right;
        infoRect.bottom = surfaceHolder.getSurfaceFrame().height();

        backGround = new BackGround(context, config, gameRect);
        infoArea = new GameInformationArea(context, info, gameRect, infoRect);

        ready();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    public void setOnUpdateHighscoreCommand(Handler foregroundHandler, Runnable callbackCommand) {
        this.foregroundHandler = foregroundHandler;
        this.callbackCommand = callbackCommand;
    }

    public GameConfig getGameConfig() {
        return this.config;
    }

    public GameStates getGameStates() {
        return this.info.getGameStates();
    }

    public PlayRecord getHighScore() {
        return this.info.getPlayRecord();
    }

    private void startExecutor() {
        // 別スレッドでメインループを開始
        processing = false;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (processing) {
                    Log.w(TAG, "frame skipped.");
                    return;
                }
                processing = true;

                try {
                    switch (info.getGameStates().getState()) {
                    // case PAUSING: // ポーズ中
                    // break;
                    // ポーズ中にCPU喰っちゃまずいのでスレッド自体を停止させるよう変更

                    case PLAYING: // プレイ中
                        doPlayProcess();
                        break;

                    case GAMEOVER: // ゲームオーバー
                        executor.shutdown();
                        doGameoverProcess();

                        // ゲームオーバー時の待機
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }

                        doReadyReplayProcess();
                        break;
                    }
                    processing = false;

                } catch (Throwable e) {
                    executor.shutdown();
                    Log.e("exception", "unknown error.", e);
                    throw new RuntimeException(e);
                }
            }
        }, 1000 / FPS, 1000 / FPS, TimeUnit.MILLISECONDS);
    }

    private void stopExecutor() {
        if (null != executor && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void initialize() {
        // ゲーム情報の準備
        info.reset();
        info.loadHighRecord(context); // ハイスコアの読み込み
        info.setRestMills(PLAY_TIME_SEC * 1000);

        // ボールマネージャの準備
        ballManager.reset();
        ballManager.create(gameRect.width() / 2, gameRect.height() / 4 * 1); // 左右の中央、上から４分の１
    }

    public void ready() {
        // 初期化
        initialize();
        info.setGameStates(GameStates.READY);

        // BGM開始
        soundController.startGameBgm();

        // １回だけ描画
        draw();
    }

    private void replay() {
        // 初期化
        initialize();

        // そのままスタート
        start();
    }

    private void start() {
        info.setGameStates(GameStates.PLAYING_PLAY);

        // ゲームモード毎の終了時間をセット
        switch (info.getGameMode()) {
        case NORMAL_MODE:
            endMills = System.currentTimeMillis() + (PLAY_TIME_SEC * 1000);
            break;
        case ENDRESS_MODE:
            endMills = -1;
            break;
        }

        // 別スレッドでメインループを開始
        startExecutor();
    }

    public void pause() {
        if (GameStates.PLAYING_PLAY != info.getGameStates()) {
            return;
        }

        info.setGameStates(GameStates.PAUSING);
        pauseMills = System.currentTimeMillis();

        // スレッド中断
        stopExecutor();
        soundController.pauseGameBgm();

        // １回だけ描画
        draw();
    }

    public void unpause() {
        info.setGameStates(GameStates.PLAYING_PLAY);
        endMills += System.currentTimeMillis() - pauseMills;

        // スレッド再開
        startExecutor();
        soundController.unpauseGameBgm();
    }

    public void stop() {
        // スレッド停止
        stopExecutor();
        soundController.stopGameBgm();

        ballManager.terminate();
        soundController.terminate();
    }

    public void touch(float x, float y) {
        switch (info.getGameStates().getState()) {
        case READY: // 開始待ち
            start();
            return;

        case PAUSING: // ポーズ中
            unpause();
            return;

        case PLAYING: // プレイ中
            synchronized (userTouch) {
                userTouch.touch(x, y);
            }
            return;

        case GAMEOVER: // ゲームオーバー
            return;

        case READY_REPLAY: // リプレイ待ち
            replay();
            return;
        }

    }

    private final void doPlayProcess() {
        long currLoopStart = System.currentTimeMillis();

        // 衝突判定：ユーザのタッチ ※プレイ中のみ
        if (GameSubState.PLAY == info.getGameStates().getSubState()) {
            collisionTouch();
        }

        // 衝突判定：壁
        collisionWalls();

        // ボールの移動
        moveBalls();

        // 有効なボール数のカウント
        int validBallCount = ballManager.getValidBallCount();
        info.setBallCount(validBallCount);

        // 残り時間の算出
        long restMills = endMills - System.currentTimeMillis();
        if (restMills < 0) {
            restMills = 0;
        }
        info.setRestMills(restMills);

        // 描画
        draw();

        // 処理時間のセット(FPS算出) ※描画は次フレームにもちこし
        info.setElapseMills(System.currentTimeMillis() - currLoopStart);

        // 終了判定
        if (0 == validBallCount) {
            switch (info.getGameStates().getSubState()) {
            case PLAY:
                info.setGameStates(GameStates.GAMEOVER_LEAVED);
                break;
            case TIMEUP:
                info.setGameStates(GameStates.GAMEOVER_TIMEUP);
                break;
            }
        } else if (0 == restMills && info.getGameMode().isNormalMode()) {
            info.setGameStates(GameStates.PLAYING_TIMEUP);
        }
    }

    private final void doGameoverProcess() {
        // 描画
        draw();

        // バイブレーション
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        // 効果音
        if (info.isUpdateTop10()) {
            soundController.soundHighScore();
        } else {
            switch (info.getGameStates().getSubState()) {
            case TIMEUP:
                soundController.soundTimeup();
                break;
            case LEAVED:
                soundController.soundGameover();
                break;
            }
        }

        // ハイスコアが更新されていれば通知
        if (info.isUpdateTop10()) {
            foregroundHandler.post(callbackCommand);
        }
    }

    private final void doReadyReplayProcess() {
        // ゲームの状態変更
        switch (info.getGameStates().getSubState()) {
        case TIMEUP:
            info.setGameStates(GameStates.READY_REPLAY_TIMEUP);
            break;
        case LEAVED:
            info.setGameStates(GameStates.READY_REPLAY_LEAVED);
            break;
        }

        // 描画
        draw();
    }

    private final void collisionTouch() {
        int ballArrayCount = ballManager.getBallArrayCount();
        IBall[] balls = ballManager.getBalls();
        boolean ballTouch = false;
        for (int i = 0; i < ballArrayCount; i++) {
            IBall ball = balls[i];
            if (userTouch.collision(ball)) {
                ballManager.bound(ball);
                for (int j = 1; j < config.getSplitCount(); j++) {
                    ballManager.split(ball);
                }
                info.addScore(1);
                ballTouch = true;
            }
        }
        if (ballTouch) {
            soundController.soundBallTouche();
        }
    }

    private final void collisionWalls() {
        int ballArrayCount = ballManager.getBallArrayCount();
        IBall[] balls = ballManager.getBalls();
        boolean wallBound = false;
        boolean ballLeave = false;
        for (int i = 0; i < ballArrayCount; i++) {
            switch (backGround.collision(balls[i])) {
            case WALL_BOUND:
                wallBound = true;
                break;

            case BALL_LEAVE:
            case MIX_COLLISION:
                ballManager.release(balls[i]);
                ballLeave = true;
                break;
            }
        }
        if (wallBound) {
            soundController.soundBallBounds();
        }
        if (ballLeave) {
            soundController.soundBallLeaved();
        }
    }

    private final void moveBalls() {
        int ballArrayCount = ballManager.getBallArrayCount();
        IBall[] balls = ballManager.getBalls();
        for (int i = 0; i < ballArrayCount; i++) {
            balls[i].move();
        }
    }

    private final void draw() {
        Canvas canvas = surfaceHolder.lockCanvas();

        backGround.draw(canvas);

        int ballArrayCount = ballManager.getBallArrayCount();
        IBall[] balls = ballManager.getBalls();
        for (int i = 0; i < ballArrayCount; i++) {
            balls[i].draw(canvas);
        }

        userTouch.draw(canvas);
        infoArea.draw(canvas);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}
