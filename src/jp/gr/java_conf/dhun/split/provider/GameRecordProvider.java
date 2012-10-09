package jp.gr.java_conf.dhun.split.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

@Deprecated
public class GameRecordProvider extends ContentProvider {
    private static final String TAG = GameRecordProvider.class.getSimpleName();
    private static final String TABLE_NAME = "game_record";
    private static final UriMatcher URI_MATCHER;
    private static final int URI_MATCH_DIR = 0;
    private static final int URI_MATCH_INSTANCE = 1;
    private static final int URI_MATCH_TOP_TOUCHED_COUNT = 2;
    private static final int URI_MATCH_TOP_MAXBALL_COUNT = 3;

    public static final String AUTHORITY = "jp.gr.java_conf.dhun.split2.provider.GameRecordProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    public static final String TOP_TOUCHED_COUNT_PATH = "topTouchedCount";
    public static final String TOP_MAXBALL_COUNT_PATH = "topMaxBallCount";

    public static final String COL_ID = "_id";
    public static final String COL_ENTRY_NAME = "entry_name";
    public static final String COL_TOUCHED_COUNT = "touched_count";
    public static final String COL_MAXBALL_COUNT = "maxball_count";
    public static final String[] ALL_COLUMN = { COL_ID, COL_ENTRY_NAME, COL_TOUCHED_COUNT, COL_MAXBALL_COUNT };

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME, URI_MATCH_DIR);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/#", URI_MATCH_INSTANCE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/" + TOP_TOUCHED_COUNT_PATH, URI_MATCH_TOP_TOUCHED_COUNT);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/" + TOP_MAXBALL_COUNT_PATH, URI_MATCH_TOP_MAXBALL_COUNT);
    }

    public static void createTable(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer();
        sql.append("create table " + TABLE_NAME + "(");
        sql.append("   " + COL_ID + "            integer primary key autoincrement");
        sql.append(" , " + COL_ENTRY_NAME + "    text");
        sql.append(" , " + COL_TOUCHED_COUNT + " integer");
        sql.append(" , " + COL_MAXBALL_COUNT + " integer");
        sql.append(")");
        db.execSQL(sql.toString());
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        db = new SplitDbOpenHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        Log.d(TAG + "-query", "match=" + String.valueOf(match) + ":" + uri);

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (URI_MATCHER.match(uri)) {
        case URI_MATCH_DIR:
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        case URI_MATCH_INSTANCE:
            qb.appendWhere(COL_ID + "=" + uri.getPathSegments().get(1));
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        case URI_MATCH_TOP_TOUCHED_COUNT:
        case URI_MATCH_TOP_MAXBALL_COUNT:
            String sort = (URI_MATCH_TOP_TOUCHED_COUNT == match ? COL_TOUCHED_COUNT : COL_MAXBALL_COUNT) + " desc";
            return qb.query(db, projection, null, null, null, null, sort, "1");

        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(TABLE_NAME, null, values);
        Uri result = Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
        Log.d(TAG + "-insert", "id=" + String.valueOf(id) + ":" + result);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException(uri.toString());
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException(uri.toString());
    }
}
