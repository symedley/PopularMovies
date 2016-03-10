package com.example.susannah.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Susannah on 2/20/2016.
 */
public class PopMoviesDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = PopMoviesDbHelper.class.getSimpleName();

    // DB name and version
    public static final String DATABASE_NAME = "popmovies.db";
    private static final int  DATABASE_VERSION = 3;

    public PopMoviesDbHelper(Context context) {
        // Context, Name,  SQLiteDatabase.CursorFactory factory, version
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "(" +
                PopMoviesContract.PopMovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " + // Boolean
                PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                //PopMoviesContract.PopMovieEntry.COLUMN_GENREIDS[] + " INTEGER NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE + " TEXT, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT + " INTEGER NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " + // Boolean
                PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_GENRES_TABLE = "CREATE TABLE " +
                PopMoviesContract.GenreEntry.TABLE_GENRES + "(" +
                PopMoviesContract.GenreEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                PopMoviesContract.GenreEntry.COLUMN_NAME + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_GENRES_TABLE);
    }

    // Upgrade the database when the version is changed. Just drop the table and recreate it.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "Upgrading databse from version " + oldVersion + " to " + newVersion +
                ". OLD DATA WILL BE DESTROYED");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.GenreEntry.TABLE_GENRES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.GenreEntry.TABLE_GENRES + "'");

        onCreate(sqLiteDatabase);
    }
}
