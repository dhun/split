package jp.gr.java_conf.dhun.split.manager.info;

import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;

/**
 * プレイ中のゲームの成績<br/>
 * 
 * @author jun
 * 
 */
public class PlayRecord extends GameRecord {
    /** ボール数(現在) */
    private int curBallCount;
    /** タッチ回数の順位 */
    private int touchedCountRank;
    /** 最大ボール数のの順位 */
    private int maxBallCountRank;

    public PlayRecord() {
        super();
        curBallCount = 0;
        touchedCountRank = 0;
        maxBallCountRank = 0;
    }

    @Override
    public void reset() {
        super.reset();
        curBallCount = 0;
    }

    /**
     * ボール数(現在)を取得します。
     * 
     * @return ボール数(現在)
     */
    public int getCurBallCount() {
        return curBallCount;
    }

    /**
     * タッチ回数を加算する
     */
    void addTouchedCount() {
        setTouchedCount(getTouchedCount() + 1);
    }

    /**
     * ボール数(現在)を設定します。
     * 
     * @param curBallCount ボール数(現在)
     */
    void setCurBallCount(int curBallCount) {
        this.curBallCount = curBallCount;
    }

    /**
     * タッチ回数の順位を取得します。
     * 
     * @return タッチ回数の順位
     */
    public int getTouchedCountRank() {
        return touchedCountRank;
    }

    /**
     * タッチ回数の順位を設定します。
     * 
     * @param touchedCountRank タッチ回数の順位
     */
    public void setTouchedCountRank(int touchedCountRank) {
        this.touchedCountRank = touchedCountRank;
    }

    /**
     * 最大ボール数のの順位を取得します。
     * 
     * @return 最大ボール数のの順位
     */
    public int getMaxBallCountRank() {
        return maxBallCountRank;
    }

    /**
     * 最大ボール数のの順位を設定します。
     * 
     * @param maxBallCountRank 最大ボール数のの順位
     */
    public void setMaxBallCountRank(int maxBallCountRank) {
        this.maxBallCountRank = maxBallCountRank;
    }
}
