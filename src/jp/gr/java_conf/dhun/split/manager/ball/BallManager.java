package jp.gr.java_conf.dhun.split.manager.ball;

import java.util.Random;

import jp.gr.java_conf.dhun.split.character.ball.BallState;
import jp.gr.java_conf.dhun.split.character.ball.IBall;
import jp.gr.java_conf.dhun.split.character.ball.IBallImageFactory;
import jp.gr.java_conf.dhun.split.character.ball.impl.ClipImageBallImageFactory;
import jp.gr.java_conf.dhun.split.character.ball.impl.RandomColorBallImageFactory;
import jp.gr.java_conf.dhun.split.character.ball.impl.SimpleBallImageFactory;
import jp.gr.java_conf.dhun.split.character.ball.impl.SimpleBitmapBall;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;

/**
 * ボールマネージャ.<br/>
 * ボールの生成と解放を管理します.<br/>
 * <code>{@link #getBalls()}</code>で取得した配列は、 <code>{@link #getBallArrayCount()}</code>までアクセスしてください. 以降の要素はすべてnullになります.<br/>
 * <br/>
 * 内部的にバッファを保持していて、無効なインスタンスを使いまわすことでガベージコレクタの発生を抑制してます.<br/>
 * ってなことを考えたが、内部的なBitmapの生成＋GCが激しすぎてあまり効果ないかも...
 * 
 * @author jun
 */
public class BallManager {
    private static final int BALL_ARRAY_EXETEND_COUNT = 64; // 内部バッファの総数
    private static final Random RANDOM = new Random();

    private final GameConfig config;
    private final IBallImageFactory ballImageFactory;
    private IBall[] balls; // ボール配列
    private int ballArrayCount; // ボール配列の「有効」な要素数
    private boolean nextRightBound;

    public BallManager(Context context, GameConfig config) {
        this.config = config;
        switch (config.getBallFaceType()) {
        case RANDOM_COLOR:
            ballImageFactory = new RandomColorBallImageFactory(context, config);
            break;
        case IMAGE_CLIP:
            ballImageFactory = new ClipImageBallImageFactory(context, config);
            break;
        default:
            ballImageFactory = new SimpleBallImageFactory(context, config);
        }

        // ボール配列の初期化
        reset();
    }

    public void terminate() {
    }

    /**
     * ボール配列を取得します.
     * 
     * @return ボール配列
     */
    public IBall[] getBalls() {
        return balls;
    }

    /**
     * ボール配列の要素数を取得します.
     * 
     * @return ボール配列の要素数
     */
    public int getBallArrayCount() {
        return ballArrayCount;
    }

    /**
     * 有効なボールの数を取得します.
     * 
     * @return 有効なボールの数
     */
    public int getValidBallCount() {
        int validBallCount = 0;
        for (int i = 0; i < ballArrayCount; i++) {
            if (balls[i].isValid()) {
                validBallCount++;
            }
        }
        return validBallCount;
    }

    /**
     * ボール配列を初期化します.
     */
    public void reset() {
        // for (int i = 0; i < ballArrayCount; i++) {
        // balls[i].setBallState(BallState.LEAVED);
        // }
        ballArrayCount = 0;
        balls = new IBall[0];
        extendBallArray();
    }

    /**
     * ボール配列を拡張します.
     */
    private void extendBallArray() {
        IBall[] tmpBalls = new IBall[balls.length + BALL_ARRAY_EXETEND_COUNT];
        System.arraycopy(balls, 0, tmpBalls, 0, balls.length);
        balls = tmpBalls;
    }

    /**
     * ボールを生成します.
     * 
     * @param x X座標
     * @param y Y座標
     * @return 生成したボール. 内部バッファを超えた場合はnull.
     */
    public IBall create(float x, float y) {
        IBall newBall = findBallByState(BallState.LEAVED);
        if (null == newBall) {
            if (BALL_ARRAY_EXETEND_COUNT <= ballArrayCount) {
                extendBallArray();
            }
            newBall = new SimpleBitmapBall(config.getBallRadius());
            balls[ballArrayCount++] = newBall;
        }

        newBall.setBallState(BallState.NORMAL);
        newBall.setImage(ballImageFactory.next());
        newBall.setLocation(x, y);
        newBall.setVectorX(0);
        newBall.setVectorY(0);

        return newBall;
    }

    /**
     * ボールを跳ね返します.
     * 
     * @param ball ボール
     */
    public void bound(IBall ball) {
        nextRightBound = !nextRightBound;
        ball.setVectorX((RANDOM.nextInt(7) + 3) * (nextRightBound ? +1 : -1)); // (+/-)3...7
        ball.setVectorY(-10 - RANDOM.nextFloat() * 3); // -10...-13
    }

    /**
     * ボールを分裂します.
     * 
     * @param ball 分裂対象のボール
     * @return 分裂したボール. 内部バッファを越えた場合はnull.
     */
    public IBall split(IBall ball) {
        IBall newBall = findBallByState(BallState.LEAVED);
        if (null == newBall) {
            if (BALL_ARRAY_EXETEND_COUNT <= ballArrayCount) {
                extendBallArray();
            }
            newBall = ball.clone();
            balls[ballArrayCount++] = newBall;
        }

        newBall.setImage(ballImageFactory.next());
        newBall.setBallState(BallState.SPLITED);
        newBall.setX(ball.getX());
        newBall.setY(ball.getY());
        newBall.setVectorX(ball.getVectorX());
        newBall.setVectorY(ball.getVectorY());
        bound(newBall);

        return newBall;
    }

    /**
     * バッファから指定された状態のボールを探します.
     * 
     * @param ballState ボールの状態
     * @return 見つけたボール. 見つからなかった場合はnull.
     */
    private IBall findBallByState(BallState ballState) {
        for (int i = 0; i < ballArrayCount; i++) {
            IBall ball = balls[i];
            if (ballState == ball.getBallState()) {
                return ball;
            }
        }
        return null;
    }

    /**
     * ボールを解放します.
     * 
     * @param ball ボール
     */
    public void release(IBall ball) {
    }
}
