package com.example.susannah.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Susannah on 2/22/2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(PopMoviesDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        deleteTheDatabase();
        SQLiteDatabase db = new PopMoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        assertTrue("Database not open!", db.isOpen());
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        ContentValues testValues = TestUtilities.createPopMovieValues();
        long locationRowId;
        locationRowId = db.insert(PopMoviesContract.PopMovieEntry.TABLE_POPMOVIES, null, testValues);
        assertTrue("Failure to insert Pop Movie values", locationRowId != -1);

        // Genre table
         testValues = TestUtilities.createGenreValues();
        locationRowId = db.insert(PopMoviesContract.GenreEntry.TABLE_GENRES, null, testValues);
        assertTrue("Failure to insert Genre values", locationRowId != -1);
        db.close();
    }
}
