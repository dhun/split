package jp.gr.java_conf.dhun.split.persistence.entity;

import android.graphics.Bitmap;

/**
 * ボールのイメージ
 * 
 * @author jun
 * 
 */
public class BallImage implements IEntity<Long> {
    /** ID */
    private Long id;
    /** ビットマップ */
    private Bitmap ballBitmap;

    @Override
    public Long getPk() {
        return getId();
    }

    /**
     * IDを取得します。
     * 
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * IDを設定します。
     * 
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * ビットマップを取得します。
     * 
     * @return ビットマップ
     */
    public Bitmap getBallBitmap() {
        return ballBitmap;
    }

    /**
     * ビットマップを設定します。
     * 
     * @param ballBitmap ビットマップ
     */
    public void setBallBitmap(Bitmap ballBitmap) {
        this.ballBitmap = ballBitmap;
    }
}
