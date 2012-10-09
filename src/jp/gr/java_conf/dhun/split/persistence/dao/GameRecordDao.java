package jp.gr.java_conf.dhun.split.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dhun.split.manager.GameMode;
import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameRecordDao extends AbstractBaseDao<GameRecord, Long> {
    private static final String TAG = GameRecordDao.class.getSimpleName();

    private static final String TABLE_NAME = "game_record";
    private static final String COL_ID = "_id";
    private static final String COL_GAME_MODE = "game_mode";
    private static final String COL_ENTRY_NAME = "entry_name";
    private static final String COL_TOUCHED_COUNT = "touched_count";
    private static final String COL_MAXBALL_COUNT = "maxball_count";
    private static final String[] ALL_COLUMN = { COL_ID, COL_GAME_MODE, COL_ENTRY_NAME, COL_TOUCHED_COUNT, COL_MAXBALL_COUNT };

    public static void createTable(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer();
        sql.append("create table " + TABLE_NAME + "(");
        sql.append("   " + COL_ID + "            integer primary key autoincrement");
        sql.append(" , " + COL_GAME_MODE + "     text");
        sql.append(" , " + COL_ENTRY_NAME + "    text");
        sql.append(" , " + COL_TOUCHED_COUNT + " integer");
        sql.append(" , " + COL_MAXBALL_COUNT + " integer");
        sql.append(")");

        Log.i(TAG, "createTable: " + sql.toString());
        db.execSQL(sql.toString());
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (1 == oldVersion && 2 == newVersion) {
            StringBuffer sql = new StringBuffer();
            sql.append("alter table " + TABLE_NAME + " add ");
            sql.append(" , " + COL_GAME_MODE + "     text");

            Log.i(TAG, "upgradeTable: " + sql.toString());
            db.execSQL(sql.toString());
        } else {
            Log.i(TAG, "upgradeTable: noop");
        }
    }

    public GameRecordDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllColumnNames() {
        return ALL_COLUMN;
    }

    @Override
    protected WhereClause buildByPkWhereClause(Long pk) {
        WhereClause whereClause = new WhereClause();
        whereClause.setSelection(COL_ID + "=" + pk);
        return whereClause;
    }

    @Override
    protected GameRecord mapToEntity(Cursor cursor) {
        GameRecord entity = new GameRecord();
        entity.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
        entity.setGameMode(GameMode.valueOf(cursor.getString(cursor.getColumnIndex(COL_GAME_MODE))));
        entity.setEntryName(cursor.getString(cursor.getColumnIndex(COL_ENTRY_NAME)));
        entity.setTouchedCount(cursor.getInt(cursor.getColumnIndex(COL_TOUCHED_COUNT)));
        entity.setMaxBallCount(cursor.getInt(cursor.getColumnIndex(COL_MAXBALL_COUNT)));
        return entity;
    }

    @Override
    protected ContentValues mapToContentValues(GameRecord entity) {
        ContentValues values = new ContentValues();
        values.put(COL_GAME_MODE, entity.getGameMode().name());
        values.put(COL_ENTRY_NAME, entity.getEntryName());
        values.put(COL_TOUCHED_COUNT, entity.getTouchedCount());
        values.put(COL_MAXBALL_COUNT, entity.getMaxBallCount());
        return values;
    }

    public GameRecord findTop(GameMode gameMode) {
        Log.d(getTag(), "findTop: gameMode=[" + gameMode + "]");

        GameRecord result = new GameRecord();
        List<GameRecord> temps;

        // タッチ回数のトップを取得
        temps = listByGameMode(gameMode, 1, COL_TOUCHED_COUNT);
        if (!temps.isEmpty()) {
            result.setTouchedCount(temps.get(0).getTouchedCount());
        }

        // 最大ボール数のトップを取得
        temps = listByGameMode(gameMode, 1, COL_MAXBALL_COUNT);
        if (!temps.isEmpty()) {
            result.setMaxBallCount(temps.get(0).getMaxBallCount());
        }

        return result;
    }

    public List<GameRecord> listTopTouchedCount(GameMode gameMode, int limit) {
        Log.d(getTag(), "listTopTouchedCount: gameMode=[" + gameMode + "], limit=[" + limit + "]");
        return listByGameMode(gameMode, limit, COL_TOUCHED_COUNT);
    }

    public List<GameRecord> listTopMaxBallCount(GameMode gameMode, int limit) {
        Log.d(getTag(), "listTopTouchedCount: gameMode=[" + gameMode + "], limit=[" + limit + "]");
        return listByGameMode(gameMode, limit, COL_MAXBALL_COUNT);
    }

    private List<GameRecord> listByGameMode(GameMode gameMode, int limit, String sortColumn) {
        Log.d(getTag(), "listByGameMode: gameMode=[" + gameMode + "], limit=[" + limit + "], sortColumn=[" + sortColumn + "]");

        Cursor cursor = null;
        try {
            String selection = COL_GAME_MODE + "= ?";
            String[] selectionArg = new String[] { gameMode.name() };
            cursor = getDatabase().query(getTableName(), ALL_COLUMN, selection, selectionArg, null, null, sortColumn + " desc", String.valueOf(limit));

            List<GameRecord> results = new ArrayList<GameRecord>(cursor.getCount());
            while (cursor.moveToNext()) {
                results.add(mapToEntity(cursor));
            }
            return results;

        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
