package jp.gr.java_conf.dhun.split.character.ball.impl;

import jp.gr.java_conf.dhun.split.character.ball.BallState;
import jp.gr.java_conf.dhun.split.character.ball.IBall;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * ビットマップで表現したボール<br/>
 * 
 * @author jun
 */
public class SimpleBitmapBall implements IBall {
    private final float r; // ボールの半径

    private Bitmap bitmap;
    private BallState ballState = BallState.NORMAL;
    private float x; // 中心のX座標
    private float y; // 中心のY座標
    private float vectorX; // ベクトルのX座標
    private float vectorY; // ベクトルのY座標

    /**
     * コンストラクタ<br/>
     * ボールマネージャから呼び出すため、スコープはパッケージプライベート
     * 
     * @param r ボールの半径
     */
    public SimpleBitmapBall(float r) {
        this.r = r;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        if (!isValid()) {
            return;
        }

        x += vectorX;
        y += vectorY;
        vectorX -= vectorX * 0.01f;
        vectorY += G / 30;
    }

    public void draw(Canvas canvas) {
        if (!isValid()) {
            return;
        }

        // ビットマップの描画
        // 左上の座標をパラメータとして与える
        canvas.drawBitmap(bitmap, x - r, y - r, (Paint) null);

        ballState = BallState.NORMAL;
    }

    /**
     * ボールが有効であるかを判定します.
     * 
     * @return 有効であればtrue.
     */
    public boolean isValid() {
        return BallState.LEAVED != ballState;
    }

    @Override
    public SimpleBitmapBall clone() {
        try {
            return (SimpleBitmapBall) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e(getClass().getSimpleName(), "unknown error.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        String format = "%s [x=%6.2f, y=%6.2f, state=%-8s]";
        return String.format(format, getClass().getSimpleName(), x, y, ballState);
    }

    /**
     * 画像を取得します。
     * 
     * @return 画像
     */
    public Bitmap getImage() {
        return this.bitmap;
    }

    /**
     * 画像を設定します。
     * 
     * @param bitmap 画像
     */
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * ballStateを取得します。
     * 
     * @return ballState
     */
    public BallState getBallState() {
        return ballState;
    }

    /**
     * ballStateを設定します。
     * 
     * @param ballState ballState
     */
    public void setBallState(BallState ballState) {
        this.ballState = ballState;
    }

    /**
     * rを取得します。
     * 
     * @return r
     */
    public float getR() {
        return r;
    }

    /**
     * xを取得します。
     * 
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * xを設定します。
     * 
     * @param x x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * yを取得します。
     * 
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * yを設定します。
     * 
     * @param y y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * vectorXを取得します。
     * 
     * @return vectorX
     */
    public float getVectorX() {
        return vectorX;
    }

    /**
     * vectorXを設定します。
     * 
     * @param vectorX vectorX
     */
    public void setVectorX(float vectorX) {
        this.vectorX = vectorX;
    }

    /**
     * vectorYを取得します。
     * 
     * @return vectorY
     */
    public float getVectorY() {
        return vectorY;
    }

    /**
     * vectorYを設定します。
     * 
     * @param vectorY vectorY
     */
    public void setVectorY(float vectorY) {
        this.vectorY = vectorY;
    }
}
