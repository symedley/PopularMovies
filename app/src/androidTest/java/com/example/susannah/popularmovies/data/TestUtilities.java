package com.example.susannah.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Susannah on 2/22/2016.
 */
public class TestUtilities extends AndroidTestCase {

    // Dates are stored as strings in the format that they come back from TMDB JSON
    // they could be translated into long, but I haven't needed to yet
    static final String TEST_DATE = "2014-12-20";
    // static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    static final String TEST_POSTERPATH = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg "; //(Interstellar)
    static final String TEST_TITLE = "Interstellar";
    static final int TEST_TMDID = 157336;
    static final String TEST_BACKDROPPATH = "\\/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg";

    static ContentValues createPopMovieValues() {
        ContentValues movieValues = new ContentValues();
// _ID will be populated automatically, right?
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH,
                TEST_POSTERPATH); //(Interstellar)
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ADULT, 0); // boolean as int
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW,
                "Silly humans confuse themselves with paradoxical timetravel.");
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE, TEST_DATE); // String
// movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_GENREIDS,  // TODO
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TMDID, TEST_TMDID); // The Movie Database ID, an int
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE, TEST_TITLE);
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG, "EN");
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TITLE, TEST_TITLE );
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH, TEST_BACKDROPPATH);
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY, 15.274595); // float
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT, 5000); // int
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO, 0); // boolean as int
        movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE, 9.99); // float

        return movieValues;
    }


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /*
     * Whatever the data structure, compare the Cursor with the ContentValues and assert that they match
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertPopMovieValues(Context context) {
        PopMoviesDbHelper popMoviesDbHelper = new PopMoviesDbHelper(context);
        SQLiteDatabase db = popMoviesDbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createPopMovieValues();

        long locationRowId;
        locationRowId = db.insert(PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES, null, testValues);

        assertTrue("Failure to insert Pop Movie values", locationRowId != -1);
        return locationRowId;
    }
}
