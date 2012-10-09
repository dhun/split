package jp.gr.java_conf.dhun.split.persistence.entity;

import jp.gr.java_conf.dhun.split.manager.GameMode;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * ゲームの成績<br/>
 * 
 * @author jun
 */
public class GameRecord implements IEntity<Long> {
    /** ID */
    private Long id;
    /** ゲームモード */
    private GameMode gameMode;
    /** エントリネーム */
    private String entryName;
    /** タッチ回数 */
    private int touchedCount;
    /** ボール数(最高) */
    private int maxBallCount;

    public GameRecord() {
        reset();
    }

    public void reset() {
        id = null;
        gameMode = null;
        entryName = null;
        touchedCount = 0;
        maxBallCount = 0;
    }

    public void store(Context context) {
        // ContentResolver resolver = context.getContentResolver();
        // Uri uri = GameRecordProvider.CONTENT_URI;
        //
        // ContentValues values = new ContentValues();
        // values.put(GameRecordProvider.COL_USER_NAME, entryName);
        // values.put(GameRecordProvider.COL_SPLITED_COUNT, touchedCount);
        // values.put(GameRecordProvider.COL_MAXBALL_COUNT, maxBallCount);
        // resolver.insert(uri, values);

        SQLiteDatabase db = new SplitDbOpenHelper(context).getWritableDatabase();
        try {
            db.beginTransaction();
            GameRecordDao gameRecordDao = new GameRecordDao(db);
            gameRecordDao.insert(this);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

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
     * ゲームモードを取得します。
     * 
     * @return ゲームモード
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * ゲームモードを設定します。
     * 
     * @param gameMode ゲームモード
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * エントリネームを取得します。
     * 
     * @return エントリネーム
     */
    public String getEntryName() {
        return entryName;
    }

    /**
     * エントリネームを設定します。
     * 
     * @param entryName エントリネーム
     */
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    /**
     * タッチ回数を取得します。
     * 
     * @return タッチ回数
     */
    public int getTouchedCount() {
        return touchedCount;
    }

    /**
     * タッチ回数を設定します。
     * 
     * @param touchedCount タッチ回数
     */
    public void setTouchedCount(int touchedCount) {
        this.touchedCount = touchedCount;
    }

    /**
     * ボール数(最高)を取得します。
     * 
     * @return ボール数(最高)
     */
    public int getMaxBallCount() {
        return maxBallCount;
    }

    /**
     * ボール数(最高)を設定します。
     * 
     * @param maxBallCount ボール数(最高)
     */
    public void setMaxBallCount(int maxBallCount) {
        this.maxBallCount = maxBallCount;
    }
}
