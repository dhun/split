package jp.gr.java_conf.dhun.split.manager.info;

/**
 * ゲームの状態
 */
public enum GameState {
    /** 開始待ち */
    READY,

    /** プレイ中 */
    PLAYING,

    /** ポーズ中 */
    PAUSING,

    /** ゲームオーバー */
    GAMEOVER,

    /** リプレイ待ち */
    READY_REPLAY;
}
