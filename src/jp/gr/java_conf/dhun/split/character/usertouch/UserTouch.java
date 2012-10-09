package jp.gr.java_conf.dhun.split.character.usertouch;

import jp.gr.java_conf.dhun.split.character.ball.BallState;
import jp.gr.java_conf.dhun.split.character.ball.IBall;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

/**
 * ユーザタッチ
 * 
 * @author jun
 * 
 */
public class UserTouch {
    /** タッチされてから衝突判定を行うフレーム数 */
    private static final int DEFAULT_ALIVE_FRAME_COUNT = 5;

    private final Paint paint;

    private UserTouchState state = UserTouchState.INVALID;
    private final float r = 50; // 有効範囲
    private float x; // 中心のX座標
    private float y; // 中心のY座標
    private int aliveFrameCount; // 生存フレーム数. 衝突判定を行うフレーム数の残り

    public UserTouch() {
        // どうすりゃ見た目がよくなるんだろうか
        // 波紋っぽくしたかったけど面倒でやめた
        paint = new Paint();
        Shader s = new RadialGradient(x, y, r, Color.GREEN, Color.YELLOW, Shader.TileMode.CLAMP);
        paint.setShader(s);
        paint.setAlpha(100);
    }

    /**
     * タッチされたときに呼び出す処理<br/>
     * 状態を「有効」にして、生存フレーム数を初期化する.
     * 
     * @param x x座標
     * @param y y座標
     */
    public void touch(float x, float y) {
        this.x = x;
        this.y = y;
        state = UserTouchState.VALID;
        aliveFrameCount = DEFAULT_ALIVE_FRAME_COUNT;
    }

    /**
     * 衝突処理<br/>
     * 処理対象とするボールは「通常」のみ.<br/>
     * ⇒ 「分裂したて」は対象外
     * 
     * @param ball ボール
     * @return ボールと衝突した場合はtrue.
     */
    public boolean collision(IBall ball) {
        if (BallState.NORMAL != ball.getBallState()) {
            return false;
        }
        if (UserTouchState.INVALID == state) {
            return false;
        }

        // ピタゴラスの定理を使った衝突判定
        // こんなやりかた知らなかった...
        float diffX = ball.getX() - this.x;
        float diffY = ball.getY() - this.y;
        float judge = ball.getR() + this.r;
        if ((diffX * diffX) + (diffY * diffY) > (judge * judge)) {
            return false;
        }

        // ボールの状態を「タッチされた 」に変更
        ball.setBallState(BallState.TOUCHED);

        // ユーザタッチの状態を「無効」に変更
        state = UserTouchState.INVALID;

        return true;
    }

    /**
     * 描画<br/>
     * 
     * @param canvas キャンバス
     */
    public void draw(Canvas canvas) {
        if (UserTouchState.VALID != state) {
            return;
        }

        // // 円の描画
        // // パラメータは中心座標
        // canvas.drawCircle(x, y, r, paint);

        // 状態が「有効」なら生存フレーム数を減らす
        if (UserTouchState.VALID == state) {
            if (0 == --aliveFrameCount) {
                // 生存フレーム数がなくなったら「無効」に変更
                state = UserTouchState.INVALID;
            }
        }
    }

    @Override
    public String toString() {
        String format = "%s [x=%6.2f, y=%6.2f, state=%-8s]";
        return String.format(format, getClass().getSimpleName(), x, y, state);
    }
}
