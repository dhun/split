package jp.gr.java_conf.dhun.split.character.ball.impl;

import jp.gr.java_conf.dhun.split.character.ball.IBallImageFactory;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * ボールイメージファクトリの抽象実装.
 * 
 * @author j.hosoya
 */
public abstract class AbstractBallImageFactory implements IBallImageFactory {

    private final GameConfig config;

    public AbstractBallImageFactory(Context context, GameConfig config) {
        this.config = config;
    }

    protected Bitmap resizeImage(Bitmap srcBitmap) {
        int size = config.getBallRadius() * 2;

        Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        Rect dstRect = new Rect(0, 0, size, size);
        Bitmap dstBitmap = Bitmap.createBitmap(size, size, srcBitmap.getConfig());
        Canvas dstCanvas = new Canvas(dstBitmap);
        dstCanvas.drawBitmap(srcBitmap, srcRect, dstRect, (Paint) null);

        return dstBitmap;
    }
}
