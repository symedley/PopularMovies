package com.example.susannah.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Susannah on 2/20/2016.
 * <p/>
 * Defines the database structure for Popular Movies.
 */
public class PopMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.susannah.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class PopMovieEntry implements BaseColumns {
        // Table name
        public static final String TABLE_POPMOVIES = "popmovies";
        // Columns
        // Because of BaseColumns, I don't think I need to define _ID
        public static final String COLUMN_POSTERPATH = "PosterPath";
        public static final String COLUMN_POSTERPATHURI = "PosterPathUri";
        public static final String COLUMN_ADULT = "Adult";
        public static final String COLUMN_OVERVIEW = "Overview";
        public static final String COLUMN_RELEASEDATE = "ReleaseDate";
        // Genre IDs might need to be it's own separate table, since it's an array
        // public static final String COLUMN_GENREIDS = "GenreIds";
        public static final String COLUMN_TMDID = "TmdId";
        public static final String COLUMN_ORIGTITLE = "OrigTitle";
        public static final String COLUMN_ORIGLANG = "OrigLang";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_BACKDROPPATH = "BackdropPath";
        public static final String COLUMN_POPULARITY = "Popularity";
        public static final String COLUMN_VOTECOUNT = "VoteCount";
        public static final String COLUMN_VIDEO = "Video";
        public static final String COLUMN_VOTEAVERAGE = "VoteAverage";
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_POPMOVIES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_POPMOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_POPMOVIES;

        // for building URIs on insertion
        public static Uri buildPopMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs by movie title
        public static Uri buildPopMoviesTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
    }

    /**
     * GenreEntry - one entry in the Genres table of the database.
     * <p/>
     * Does not need to implement BaseColumns because each genre comes with its own
     * id integer.
     */
    public static final class GenreEntry {
        public static final String TABLE_GENRES = "genres";

        // Columns
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_NAME = "Name";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_GENRES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_GENRES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_GENRES;

        // for building URIs on insertion
        public static Uri buildGenresUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs by genre name
        public static Uri buildGenresTitle(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }
    }

    /**
     * MovieToGenresEntry - one entry in the table that maps Movie _IDs to Genre IDs.
     * <p/>
     * Does not need to implement BaseColumns because it's a mapping of one
     * integer ID to another integer ID
     */
    public static final class MovieToGenresEntry {
        public static final String TABLE_MOVIE_TO_GENRES = "movieToGenres";

        // Columns
        public static final String COLUMN_MOVIE_ID = "MovieID";
        public static final String COLUMN_GENRE_ID = "GenreID";
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIE_TO_GENRES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_TO_GENRES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_TO_GENRES;

        // for building URIs on insertion
        public static Uri buildMovieGenresUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs by genre name
        public static Uri buildMovieByGenreId(int genreId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(genreId)).build();
        }
    }
}