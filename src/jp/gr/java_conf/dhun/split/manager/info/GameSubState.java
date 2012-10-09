package jp.gr.java_conf.dhun.split.manager.info;

/**
 * ゲームのサブ状態
 */
public enum GameSubState {
    /** なし */
    NONE,
    /** プレイ */
    PLAY,
    /** タイムアップ */
    TIMEUP,
    /** すべてのボールが退場 */
    LEAVED;
}
