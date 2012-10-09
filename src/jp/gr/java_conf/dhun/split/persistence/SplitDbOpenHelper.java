package jp.gr.java_conf.dhun.split.persistence;

import jp.gr.java_conf.dhun.split.persistence.dao.BallImageDao;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SplitDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "split.db";
    private static final int DB_VERSION = 2;

    public SplitDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            BallImageDao.createTable(db);
            GameRecordDao.createTable(db);
        } catch (Exception e) {
            Log.e("db", "unknown error.", e);
        } finally {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            BallImageDao.upgradeTable(db, oldVersion, newVersion);
            GameRecordDao.upgradeTable(db, oldVersion, newVersion);
        } catch (Exception e) {
            Log.e("db", "unknown error.", e);
        } finally {
        }
    }
}