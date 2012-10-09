package jp.gr.java_conf.dhun.split.character.ball;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * ボールのインターフェース<br/>
 * 
 * @author jun
 * 
 */
public interface IBall extends Cloneable {
    /** 重力加速度 */
    public static final float G = 9.8f;

    /**
     * 位置を設定します.
     * 
     * @param x X座標
     * @param y Y座標
     */
    public void setLocation(float x, float y);

    /**
     * 移動処理
     */
    public void move();

    /**
     * 描画処理
     * 
     * @param canvas
     */
    public void draw(Canvas canvas);

    /**
     * 状態が「有効」であるかを判定します<br/>
     * 
     * @return 状態が「退場」以外であればtrue.
     */
    public boolean isValid();

    /**
     * 複製します
     * 
     * @return 複製したボール
     */
    public IBall clone();

    /**
     * 画像を取得します。
     * 
     * @return 画像
     */
    public Bitmap getImage();

    /**
     * 画像を設定します。
     * 
     * @param bitmap 画像
     */
    public void setImage(Bitmap bitmap);

    /**
     * 状態を取得します。
     * 
     * @return 状態
     */
    public BallState getBallState();

    /**
     * 状態を設定します。
     * 
     * @param 状態 状態
     */
    public void setBallState(BallState ballState);

    /**
     * 半径を取得します。
     * 
     * @return 半径
     */
    public float getR();

    /**
     * 中心を示すX座標を取得します。
     * 
     * @return X座標
     */
    public float getX();

    /**
     * 中心を示すX座標を設定します。
     * 
     * @param x X座標
     */
    public void setX(float x);

    /**
     * 中心を示すY座標を取得します。
     * 
     * @return Y座標
     */
    public float getY();

    /**
     * 中心を示すY座標を設定します。
     * 
     * @param y Y座標
     */
    public void setY(float y);

    /**
     * X軸のベクトルを取得します。
     * 
     * @return X軸のベクトル
     */
    public float getVectorX();

    /**
     * X軸のベクトルを設定します。
     * 
     * @param vectorX X軸のベクトル
     */
    public void setVectorX(float vectorX);

    /**
     * Y軸のベクトルを取得します。
     * 
     * @return Y軸のベクトル
     */
    public float getVectorY();

    /**
     * Y軸のベクトルを設定します。
     * 
     * @param Y軸のベクトル vectorY
     */
    public void setVectorY(float vectorY);
}
