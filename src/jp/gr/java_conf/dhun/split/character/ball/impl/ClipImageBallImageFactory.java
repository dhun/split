package jp.gr.java_conf.dhun.split.character.ball.impl;

import java.util.Random;

import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * クリップ画像を使ったボールイメージファクトリの実装.
 * 
 * @author j.hosoya
 */
public class ClipImageBallImageFactory extends AbstractBallImageFactory {
    private final Random RANDOM = new Random();

    private final Bitmap[] ballImages;

    public ClipImageBallImageFactory(Context context, GameConfig config) {
        super(context, config);

        // クリップ画像をリサイズしながら配列へコピー
        ballImages = new Bitmap[config.getBallImages().size()];
        for (int i = 0; i < config.getBallImages().size(); i++) {
            ballImages[i] = resizeImage(config.getBallImages().get(i).getBallBitmap());
        }
    }

    @Override
    public Bitmap next() {
        // ランダムに画像を選択してボールを生成
        return ballImages[RANDOM.nextInt(ballImages.length)];
    }
}
