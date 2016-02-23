package com.example.susannah.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Susannah on 2/20/2016.
 *
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
        public static Uri buildPopMoviesUri(long id ) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs by movie title
        public static Uri buildPopMoviesTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
    }
}
