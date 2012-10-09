package jp.gr.java_conf.dhun.split.character.ball.impl;

import java.util.Random;

import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * ボールの色をランダムに決定するボールイメージファクトリの実装.
 * 
 * @author j.hosoya
 */
public class RandomColorBallImageFactory extends AbstractBallImageFactory {
    private final Random RANDOM = new Random();

    private final int ballRadius;
    private final Paint ballPaint;

    public RandomColorBallImageFactory(Context context, GameConfig config) {
        super(context, config);
        ballRadius = config.getBallRadius();

        // ボール描画用のペイント
        ballPaint = new Paint();
        ballPaint.setStyle(Style.FILL_AND_STROKE);
        ballPaint.setStrokeWidth(0);
        ballPaint.setAntiAlias(true);
    }

    @Override
    public Bitmap next() {
        int size = ballRadius * 2;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 本体を描画. 色はランダム. 透明度はなし
        ballPaint.setColor(0xff000000 | RANDOM.nextInt(0xffffff));
        float x = size / 2f;
        float y = size / 2f;
        float r = ballRadius;
        canvas.drawCircle(x, y, r, ballPaint);

        // 光沢を描画. 色は白固定
        ballPaint.setColor(Color.WHITE);
        x = size / 4f * 3f;
        y = size / 4f * 1f;
        r = ballRadius * 0.2f;
        canvas.drawCircle(x, y, r, ballPaint);

        return bitmap;
    }
}
