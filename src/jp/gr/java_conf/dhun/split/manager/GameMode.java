package jp.gr.java_conf.dhun.split.manager;

/**
 * ゲームモード
 * 
 * @author jun
 */
public enum GameMode {
    /** ノーマルモード */
    NORMAL_MODE,
    /** エンドレスモード */
    ENDRESS_MODE;

    public static final String TAG = GameMode.class.getSimpleName();

    public boolean isNormalMode() {
        return NORMAL_MODE == this;
    }

    public boolean isEndressMode() {
        return ENDRESS_MODE == this;
    }
}
