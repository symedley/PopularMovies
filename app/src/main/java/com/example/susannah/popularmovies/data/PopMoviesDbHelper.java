package com.example.susannah.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Popular Movies database helper creates the tables using SQL Strings, handles database upgrades
 * <p/>
 * Created by Susannah on 2/20/2016.
 */
class PopMoviesDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = PopMoviesDbHelper.class.getSimpleName();

    // DB name and version
    protected static final String DATABASE_NAME = "popmovies.db";
    private static final int DATABASE_VERSION = 12;

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
                PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " REAL NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE + " INTEGER NOT NULL, " + // Boolean
                "UNIQUE (" + PopMoviesContract.PopMovieEntry.COLUMN_TMDID + " ));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        // the Favorites table that stores the movie data and not just the tmdID
        // It reuses the contantcs from the PopMovieEntry class because it needs
        // to be the same as the main popMovies table.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                PopMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "(" +
                PopMoviesContract.PopMovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " + // Boolean
                PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                //Genres are handled in a different table.
                PopMoviesContract.PopMovieEntry.COLUMN_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE + " TEXT, " +
                PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH + " TEXT NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT + " INTEGER NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " + // Boolean
                PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " REAL NOT NULL, " +
                PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE + " INTEGER NOT NULL, " + // Boolean
                "UNIQUE (" + PopMoviesContract.PopMovieEntry.COLUMN_TMDID + " ));";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

        final String SQL_CREATE_GENRES_TABLE = "CREATE TABLE " +
                PopMoviesContract.GenreEntry.TABLE_GENRES + "(" +
                PopMoviesContract.GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                PopMoviesContract.GenreEntry.COLUMN_NAME + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_GENRES_TABLE);

        final String SQL_CREATE_MOVIES_TO_GENRES_TABLE = "CREATE TABLE " +
                PopMoviesContract.MovieToGenresEntry.TABLE_MOVIE_TO_GENRES + "(" +
                PopMoviesContract.MovieToGenresEntry.COLUMN_MOVIE_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.MovieToGenresEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TO_GENRES_TABLE);

        final String SQL_CREATE_MOVIE_FAVORITES = "CREATE TABLE " +
                PopMoviesContract.MovieFavoriteTmdId.TABLE_MOVIE_FAVORITE_TMDIDS + "(" +
                PopMoviesContract.MovieFavoriteTmdId.COLUMN_MOVIE_TMDID + " INTEGER NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITES);

        final String SQL_CREATE_MOVIE_IMAGES = "CREATE TABLE " +
                PopMoviesContract.MovieImages.TABLE_MOVIE_IMAGES + "(" +
                PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.MovieImages.COLUMN_IMAGE_DATA + " BLOB, "+
                "UNIQUE (" + PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID + " )); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_IMAGES);

        final String SQL_CREATE_REVIEWS = "CREATE TABLE " +
                PopMoviesContract.ReviewEntry.TABLE_REVIEWS + "(" +
                PopMoviesContract.ReviewEntry.COLUMN_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT, "+
                PopMoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT, "+
                PopMoviesContract.ReviewEntry.COLUMN_URL + " TEXT ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS);

        final String SQL_CREATE_VIDEOS = "CREATE TABLE " +
                PopMoviesContract.VideoEntry.TABLE_VIDEOS + "(" +
                PopMoviesContract.VideoEntry.COLUMN_TMDID + " INTEGER NOT NULL, " +
                PopMoviesContract.VideoEntry.COLUMN_KEY + " TEXT, "+
                PopMoviesContract.VideoEntry.COLUMN_NAME + " TEXT, "+
                PopMoviesContract.VideoEntry.COLUMN_SITE + " TEXT, "+
                PopMoviesContract.VideoEntry.COLUMN_SIZE + " TEXT, "+
                PopMoviesContract.VideoEntry.COLUMN_TYPE + " TEXT ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS);
    }

    // Upgrade the database when the version is changed. Just drop the table and recreate it.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "Upgrading databse from version " + oldVersion + " to " + newVersion +
                ". OLD DATA WILL BE DESTROYED");
        // Movies
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES + "'");
        // Favorite movies
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "'");
        //Genres
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.GenreEntry.TABLE_GENRES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.GenreEntry.TABLE_GENRES + "'");
        // Genres of movies
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieToGenresEntry.TABLE_MOVIE_TO_GENRES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.MovieToGenresEntry.TABLE_MOVIE_TO_GENRES + "'");
        // the TMD IDs of favorites. This is no longer used. Replaced by a table of move favorites.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieFavoriteTmdId.TABLE_MOVIE_FAVORITE_TMDIDS);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.MovieFavoriteTmdId.TABLE_MOVIE_FAVORITE_TMDIDS + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieFavoriteTmdId.OLD_TABLE_MOVIE_FAVORITES);
        // Poster images
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieImages.TABLE_MOVIE_IMAGES );
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.MovieImages.TABLE_MOVIE_IMAGES + "'");
        // Reviews
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.ReviewEntry.TABLE_REVIEWS);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.ReviewEntry.TABLE_REVIEWS + "'");
        // Videos
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.VideoEntry.TABLE_VIDEOS);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                PopMoviesContract.VideoEntry.TABLE_VIDEOS + "'");
        onCreate(sqLiteDatabase);
    }
}
