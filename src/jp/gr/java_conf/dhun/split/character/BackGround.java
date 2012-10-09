package jp.gr.java_conf.dhun.split.character;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.character.ball.BallState;
import jp.gr.java_conf.dhun.split.character.ball.IBall;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 背景<br/>
 * 
 * @author jun
 */
public class BackGround {
    /** 跳ね返り係数 */
    private static final float REBOUND_FOCTOR = 0.8f;

    private final Bitmap bitmap;
    private final Rect gameRect;

    public enum CollisionState {
        NONE, WALL_BOUND, BALL_LEAVE, MIX_COLLISION;
    }

    public BackGround(Context context, GameConfig config, Rect gameRect) {
        // this.context = context;
        this.gameRect = gameRect;

        // 背景画像の読み込み
        Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.background);
        int bmpWidth = srcBitmap.getWidth();
        int bmpHeight = srcBitmap.getHeight();

        // 背景画像をゲーム領域いっぱいに展開
        // 表示が荒くなるので引き伸ばさない
        bitmap = Bitmap.createBitmap(gameRect.width(), gameRect.height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int y = 0;; y += bmpHeight) {
            for (int x = 0;; x += bmpWidth) {
                canvas.drawBitmap(srcBitmap, x, y, (Paint) null);
                if (x > gameRect.width()) {
                    break;
                }
            }
            if (y > gameRect.height()) {
                break;
            }
        }
    }

    /**
     * 衝突処理<br/>
     * 有効なボールだけを処理対象とします.
     * 
     * @param ball ボール
     * @return 衝突結果
     */
    public CollisionState collision(IBall ball) {
        if (!ball.isValid()) {
            return CollisionState.NONE;
        }

        final float buff = 2f;
        final float ballX = ball.getX();
        final float ballY = ball.getY();
        final float ballR = ball.getR();
        CollisionState result = CollisionState.NONE;

        // 左右の壁との衝突判定
        if (ballX - ballR <= gameRect.left) { // ちょびっとでも触れたら
            ball.setX(gameRect.left + ballR + buff);
            ball.setVectorX(ball.getVectorX() * -REBOUND_FOCTOR);
            result = CollisionState.WALL_BOUND;

        } else if (ballX + ballR >= gameRect.right) { // ちょびっとでも触れたら
            ball.setX(gameRect.right - ballR - buff);
            ball.setVectorX(ball.getVectorX() * -REBOUND_FOCTOR);
            result = CollisionState.WALL_BOUND;
        }

        // 上下の壁との衝突判定
        if (ballY - ballR <= gameRect.top) { // ちょびっとでも触れたら
            ball.setY(gameRect.top + ballR + buff);
            ball.setVectorY(ball.getVectorY() * -REBOUND_FOCTOR);
            result = CollisionState.WALL_BOUND;

        } else if (ballY - ballR >= gameRect.bottom) {
            ball.setY(gameRect.bottom - ballR - buff);
            ball.setBallState(BallState.LEAVED); // 状態を「退場 」に変更
            if (CollisionState.NONE == result) {
                result = CollisionState.BALL_LEAVE;
            } else {
                result = CollisionState.MIX_COLLISION;
            }
        }

        return result;
    }

    /**
     * 描画<br/>
     * 
     * @param canvas キャンバス
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, (Paint) null);
    }
}
