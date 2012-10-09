package jp.gr.java_conf.dhun.split.character.ball;

/**
 * ボールの状態
 * 
 * @author jun
 * 
 */
public enum BallState {
    /** 通常 */
    NORMAL,
    /** タッチしたて */
    TOUCHED,
    /** 分裂したて */
    SPLITED,
    /** 退場 */
    LEAVED;
}
