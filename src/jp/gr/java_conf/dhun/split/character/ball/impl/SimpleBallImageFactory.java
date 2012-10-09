package jp.gr.java_conf.dhun.split.character.ball.impl;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 規定のボール画像を利用するボールイメージファクトリの実装.
 * 
 * @author j.hosoya
 */
public class SimpleBallImageFactory extends AbstractBallImageFactory {
    private final Bitmap ballImage;

    public SimpleBallImageFactory(Context context, GameConfig config) {
        super(context, config);

        // リソースを読み込んでリサイズ
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.ball);
        ballImage = resizeImage(bitmap);
    }

    @Override
    public Bitmap next() {
        return ballImage;
    }
}
