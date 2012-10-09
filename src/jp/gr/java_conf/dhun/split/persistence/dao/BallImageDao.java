package jp.gr.java_conf.dhun.split.persistence.dao;

import java.io.ByteArrayOutputStream;

import jp.gr.java_conf.dhun.split.persistence.entity.BallImage;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BallImageDao extends AbstractBaseDao<BallImage, Long> {
    private static final String TAG = BallImageDao.class.getSimpleName();

    private static final String TABLE_NAME = "ball_image";
    private static final String COL_ID = "_id";
    private static final String COL_BALL_PIXELS = "ball_pixels";
    private static final String[] ALL_COLUMN = { COL_ID, COL_BALL_PIXELS };

    public static void createTable(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer();
        sql.append("create table " + TABLE_NAME + "(");
        sql.append("   " + COL_ID + "            integer primary key autoincrement");
        sql.append(" , " + COL_BALL_PIXELS + "   blob");
        sql.append(")");

        Log.i(TAG, "createTable: " + sql.toString());
        db.execSQL(sql.toString());
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "upgradeTable: noop");
    }

    public BallImageDao(SQLiteDatabase db) {
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
    protected BallImage mapToEntity(Cursor cursor) {
        BallImage entity = new BallImage();
        entity.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
        entity.setBallBitmap(convert(cursor.getBlob(cursor.getColumnIndex(COL_BALL_PIXELS))));
        return entity;
    }

    @Override
    protected ContentValues mapToContentValues(BallImage entity) {
        ContentValues values = new ContentValues();
        values.put(COL_BALL_PIXELS, convert(entity.getBallBitmap()));
        return values;
    }

    // バイト配列をビットマップに変換
    private Bitmap convert(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    // ビットマップをバイト配列に変換
    private byte[] convert(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }
}
