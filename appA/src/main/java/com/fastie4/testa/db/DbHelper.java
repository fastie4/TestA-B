package com.fastie4.testa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fastie4.common.db.HistoryContract;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " +
            HistoryContract.HistoryEntry.TABLE_NAME + "(" +
            HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            HistoryContract.HistoryEntry.COLUMN_LINK + " TEXT, " +
            HistoryContract.HistoryEntry.COLUMN_STATUS + " INTEGER, " +
            HistoryContract.HistoryEntry.COLUMN_TIME + " INTEGER);";

    private static final String SQL_DROP_HISTORY_TABLE = "DROP TABLE IF EXISTS " +
            HistoryContract.HistoryEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_HISTORY_TABLE);
        onCreate(db);
    }
}
