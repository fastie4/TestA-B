package com.fastie4.testa.db.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fastie4.testa.db.DbHelper;
import com.fastie4.common.db.HistoryContract;

public class HistoryProvider extends ContentProvider {
    private static final int CODE_HISTORY = 1;
    private static final int CODE_HISTORY_WITH_ID = 2;
    private DbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_HISTORY:
                cursor = mOpenHelper.getReadableDatabase().query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_HISTORY:
                long _id = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, contentValues);
                if (_id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return HistoryContract.HistoryEntry.buildHistoryUriWithId(_id);
            default:
                throw new UnsupportedOperationException("What is uri id? Uri " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_HISTORY_WITH_ID:
                String _ID = uri.getLastPathSegment();
                String[] whereArgs = new String[]{_ID};
                int count = db.delete(HistoryContract.HistoryEntry.TABLE_NAME,
                        HistoryContract.HistoryEntry._ID + " = ?",
                        whereArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_HISTORY_WITH_ID:
                String _ID = uri.getLastPathSegment();
                String[] whereArgs = new String[]{_ID};
                int count = db.update(HistoryContract.HistoryEntry.TABLE_NAME,
                        contentValues,
                        HistoryContract.HistoryEntry._ID + " = ?",
                        whereArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HistoryContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, HistoryContract.HistoryEntry.TABLE_NAME, CODE_HISTORY);
        matcher.addURI(authority, HistoryContract.HistoryEntry.TABLE_NAME + "/#", CODE_HISTORY_WITH_ID);
        return matcher;
    }
}
