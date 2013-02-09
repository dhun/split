package jp.gr.java_conf.dhun.split.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.gr.java_conf.dhun.split.persistence.dao.BallImageDao;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SplitDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "split.db";
    private static final int DB_VERSION = 2;

    private static final String INITIAL_SQL = "initial.sql";

    private final Context context;

    public SplitDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        BallImageDao.createTable(db);
        GameRecordDao.createTable(db);

        executeAssetSqlFile(db, INITIAL_SQL);
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

    protected void executeAssetSqlFile(SQLiteDatabase db, String sqlFileName) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] fileNames = assetManager.list("");
            for (String fileName : fileNames) {
                if (fileName.equalsIgnoreCase(sqlFileName)) {
                    executeSqlFile(db, assetManager.open(fileName));
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void executeSqlFile(SQLiteDatabase db, InputStream sqlFile) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(sqlFile));
            db.beginTransaction();

            String sql;
            while (null != (sql = br.readLine())) {
                sql = sql.trim().replaceAll("^\\t", "");
                if (sql.length() == 0) {
                } else if (sql.startsWith("--")) {
                } else {
                    db.execSQL(sql);
                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) { // included IOException, SQLException
            String msg = "SQLファイルの実行中にエラーが発生した. 処理は継続する.";
            Log.e("db", msg, e);

        } finally {
            db.endTransaction();

            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    String msg = "SQLファイルのクローズに失敗した. 処理は継続する.";
                    Log.w("db", msg, e);
                }
            }
            if (null != sqlFile) {
                try {
                    sqlFile.close();
                } catch (IOException e) {

                }
            }
        }
    }
}
