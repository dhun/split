package jp.gr.java_conf.dhun.split.manager.info;

public class GameStates {
    public static final GameStates READY = new GameStates(GameState.READY, GameSubState.NONE);
    public static final GameStates PAUSING = new GameStates(GameState.PAUSING, GameSubState.NONE);
    public static final GameStates PLAYING_PLAY = new GameStates(GameState.PLAYING, GameSubState.PLAY);
    public static final GameStates PLAYING_TIMEUP = new GameStates(GameState.PLAYING, GameSubState.TIMEUP);
    public static final GameStates GAMEOVER_TIMEUP = new GameStates(GameState.GAMEOVER, GameSubState.TIMEUP);
    public static final GameStates GAMEOVER_LEAVED = new GameStates(GameState.GAMEOVER, GameSubState.LEAVED);
    public static final GameStates READY_REPLAY_TIMEUP = new GameStates(GameState.READY_REPLAY, GameSubState.TIMEUP);
    public static final GameStates READY_REPLAY_LEAVED = new GameStates(GameState.READY_REPLAY, GameSubState.LEAVED);

    /** ゲームの状態 */
    private final GameState state;
    /** ゲームのサブ状態 */
    private final GameSubState subState;

    private GameStates(GameState state, GameSubState subState) {
        this.state = state;
        this.subState = subState;
    }

    /**
     * ゲームの状態を取得します。
     * 
     * @return ゲームの状態
     */
    public GameState getState() {
        return state;
    }

    /**
     * ゲームのサブ状態を取得します。
     * 
     * @return ゲームのサブ状態
     */
    public GameSubState getSubState() {
        return subState;
    }

    @Override
    public String toString() {
        return "state=[" + state.name() + "], subState=[" + subState.name() + "]";
    }

    @Override
    public int hashCode() {
        return state.hashCode() + subState.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameStates)) {
            return false;
        }

        GameStates other = (GameStates) o;
        if (this.state == other.state && this.subState == other.subState) {
            return true;
        } else {
            return false;
        }
    }
}
