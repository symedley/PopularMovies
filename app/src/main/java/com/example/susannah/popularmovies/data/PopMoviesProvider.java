package com.example.susannah.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Susannah on 2/20/2016.
 */
public class PopMoviesProvider extends ContentProvider {
    private static final String LOG_TAG = PopMoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopMoviesDbHelper mOpenHelper;

    // Codes for the Uri matcher
    private static final int POPMOVIE = 100;
    private static final int POPMOVIE_WITH_ID = 200;
    private static final int GENRE = 300;
    private static final int GENRE_WITH_ID  = 400;

    //The Query builder might only be needed if you're defining a JOIN between tables
    //private static final SQLiteQueryBuilder sSQLiteQueryBuilder;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES, POPMOVIE);
        matcher.addURI(authority, PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "/#", POPMOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POPMOVIE: {
                return PopMoviesContract.PopMovieEntry.CONTENT_DIR_TYPE;
            }
            case POPMOVIE_WITH_ID: {
                return PopMoviesContract.PopMovieEntry.CONTENT_ITEM_TYPE;
            }
            case GENRE: {
                return PopMoviesContract.GenreEntry.CONTENT_DIR_TYPE;
            }
            case GENRE_WITH_ID: {
                return PopMoviesContract.GenreEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
            }
        }
    }

    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case POPMOVIE : {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case POPMOVIE_WITH_ID : {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        projection,
                        PopMoviesContract.PopMovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case GENRE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.GenreEntry.TABLE_GENRES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case GENRE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.GenreEntry.TABLE_GENRES,
                        projection,
                        PopMoviesContract.GenreEntry.COLUMN_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (sUriMatcher.match(uri)) {
            case POPMOVIE: {
                long _id = mOpenHelper.getReadableDatabase().insert(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        null,
                        contentValues);
                if (_id > 0) {
                    returnUri = PopMoviesContract.PopMovieEntry.buildPopMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case GENRE: {
                long _id = mOpenHelper.getReadableDatabase().insert(
                        PopMoviesContract.GenreEntry.TABLE_GENRES,
                        null,
                        contentValues);
                if (_id > 0) {
                    returnUri = PopMoviesContract.GenreEntry.buildGenresUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POPMOVIE:
                count = db.delete(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES, selection, selectionArgs);
                // reset _ID ?
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "'");
                break;
            case POPMOVIE_WITH_ID:
                count = db.delete(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        PopMoviesContract.PopMovieEntry._ID + " = ?",
                        new String [] { String.valueOf(ContentUris.parseId(uri))});
                // reset _ID ?
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (count != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case POPMOVIE: {
                count = mOpenHelper.getReadableDatabase().update(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case POPMOVIE_WITH_ID: {
                count = mOpenHelper.getReadableDatabase().update(
                        PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                        contentValues,
                        PopMoviesContract.PopMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri " + uri);
            }
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        final  SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count = 0;
        switch (match) {
            case POPMOVIE:
                db.beginTransaction();;

                try {
                    for (ContentValues value: contentValues) {
                        if (value == null ) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(
                                    PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES,
                                    null,
                                    value);
                        } catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            PopMoviesContract.PopMovieEntry.COLUMN_TITLE)
                                    + " but perhaps the value is already in the database.");
                        }
                        if (_id != -1) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, contentValues);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.v(LOG_TAG,  Thread.currentThread().getStackTrace()[2].getMethodName() + " bulk instered "+count+" records");
        }
        return count;
    }
}
