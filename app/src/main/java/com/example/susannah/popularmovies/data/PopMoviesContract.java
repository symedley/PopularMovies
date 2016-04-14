package com.example.susannah.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Susannah on 2/20/2016.
 * <p/>
 * Defines the database structure for Popular Movies.
 * popmovies = table of all the movies stored and all their relevant data
 * genres = table of the possible genres with their int IDs and their String names (approximately 20)
 * movieToGenres = a mapping of movie ID to genre ID. Each movie ID can have many entries.
 * movieFavorites = a simple list of Movie IDs that the user has designated favorites.
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
        public static final String COLUMN_IS_FAVORITE = "IsFavorite";
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
        // this uses the _ID column from BaseColumns, not the TmdId
        public static Uri buildPopMoviesUriBy_Id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs by movie title
        public static Uri buildPopMoviesTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
    }

    /**
     * FavoriteMovieEntry - same data as in the PopMovieEntry. This will be a persistent table
     * that holds the movies that the user has "favorited".
     *
     * The column name constants from PopMovieEntry are reused.
     *
     * Note that the column _ID here will be the database generated ID of
     * the popMovies table entry. TODO check this
     */
    public static final class FavoriteMovieEntry implements BaseColumns {
        // Table name
        public static final String TABLE_FAVORITE_MOVIES = "favoriteMovies";
        // Columns are the same as PopMovieEntry, so just reuse those.
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAVORITE_MOVIES).build();

        // Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;

        // this is the _ID that was assigned when the movie was put into the popMovies table
        // This should ONLY BE USED in the Provider. Elsewhere, use TMD ID
        public static Uri buildFavoriteMoviesUriBy_Id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildFavoriteMoviesUriByTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
        public static Uri buildAllFavoriteMoviesUri() {
            return CONTENT_URI.buildUpon().build();
        }
    }

        /**
     * GenreEntry - one entry in the Genres table of the database.
     * <p/>
     * Does not need to implement BaseColumns because each genre comes with its own
     * id integer.
     */
    public static final class GenreEntry implements  BaseColumns {
        public static final String TABLE_GENRES = "genres";

        // Columns
        public static final String COLUMN_GENRE_ID = "Genre_Id";
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
            // Use this ONLY in the content provider. Elsewhere, use TMD ID
        public static Uri buildGenresUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for TMD ID searches, use selection arguments

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
    public static final class MovieToGenresEntry implements BaseColumns {
        public static final String TABLE_MOVIE_TO_GENRES = "movieToGenres";

        // Columns
        public static final String COLUMN_MOVIE_TMDID = "MovieID";
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

    /**
     * MovieFavoriteTmdId - if the TMD Movie ID is in here, it is a user favorite. This allows
     * persistence when changing the sort order between most popular and highest rated.
     * Because the latter changes the contents of the database, if the favorites information
     * were only in the PopMovies table, it would be lost with each refresh.
     * <p/>
     * Does not need to implement BaseColumns because it's just an integer.
     */
    public static final class MovieFavoriteTmdId implements BaseColumns {
        public static final String TABLE_MOVIE_FAVORITE_TMDIDS = "movieFavoriteTmdIds";

        public static final String OLD_TABLE_MOVIE_FAVORITES = "movieFavorites";

        // Columns
        public static final String COLUMN_MOVIE_TMDID = "MovieID";
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIE_FAVORITE_TMDIDS).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_FAVORITE_TMDIDS;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_FAVORITE_TMDIDS;

        // for building URIs by Movie ID (the TmdID returned by theMovieDB)
        public static Uri buildMovieFavoritesIdUri(long tmdId) {
            return ContentUris.withAppendedId(CONTENT_URI, tmdId);
        }

        // This is for finding all PopMovie entries that have their tmdID in the favorites table.
        public static Uri buildMovieFavoritesAll() {
            return CONTENT_URI;
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
