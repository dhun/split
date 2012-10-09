package jp.gr.java_conf.dhun.split.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dhun.split.persistence.entity.IEntity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class AbstractBaseDao<T extends IEntity<PK>, PK> implements IDao<T, PK> {
    private final SQLiteDatabase db;

    protected abstract String getTableName();

    protected abstract String[] getAllColumnNames();

    protected abstract WhereClause buildByPkWhereClause(PK pk);

    protected abstract ContentValues mapToContentValues(T entity);

    protected abstract T mapToEntity(Cursor cursor);

    protected String getTag() {
        return getClass().getSimpleName();
    }

    protected String getNullColumnHack() {
        return null;
    }

    public AbstractBaseDao(SQLiteDatabase db) {
        this.db = db;
    }

    protected SQLiteDatabase getDatabase() {
        return db;
    }

    @Override
    public T findByPk(PK pk) {
        WhereClause whereClause = buildByPkWhereClause(pk);
        Log.d(getTag(), "findByPk: " + whereClause);

        Cursor cursor = null;
        try {
            cursor = db.query(getTableName(), getAllColumnNames(), whereClause.getSelection(), whereClause.getSelectionArgs(), null, null, null);
            if (cursor.moveToNext()) {
                return mapToEntity(cursor);
            } else {
                return null;
            }

        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public List<T> findAll() {
        Log.d(getTag(), "findAll:");

        Cursor cursor = null;
        try {
            cursor = db.query(getTableName(), getAllColumnNames(), null, null, null, null, null);
            List<T> results = new ArrayList<T>();
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

    @Override
    public PK insert(T entity) {
        Log.d(getTag(), "insert:");

        ContentValues cv = mapToContentValues(entity);
        long rowid = db.insert(getTableName(), getNullColumnHack(), cv);

        Log.d(getTag(), "insert: rowid=[" + rowid + "]");
        return entity.getPk();
    }

    @Override
    public int update(T entity) {
        WhereClause whereClause = buildByPkWhereClause(entity.getPk());
        Log.d(getTag(), "update: whereClause=[" + whereClause + "]");

        ContentValues cv = mapToContentValues(entity);
        int affect = db.update(getTableName(), cv, whereClause.getSelection(), whereClause.getSelectionArgs());

        Log.d(getTag(), "update: affect=[" + affect + "]");
        return affect;
    }

    @Override
    public int delete(T entity) {
        WhereClause whereClause = buildByPkWhereClause(entity.getPk());
        Log.d(getTag(), "delete: whereClause=[" + whereClause + "]");

        int affect = db.delete(getTableName(), whereClause.getSelection(), whereClause.getSelectionArgs());

        Log.d(getTag(), "delete: affect=[" + affect + "]");
        return affect;
    }
}
