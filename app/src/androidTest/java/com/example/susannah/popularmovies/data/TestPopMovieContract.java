package com.example.susannah.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Susannah on 2/21/2016.
 */
public class TestPopMovieContract extends AndroidTestCase {
    private static final String TEST_TITLE = "My Neighbor Totoro";
    // Weather uses a / before the location name. Do i need the /? TODO
    // private static final String TEST_TITLE = "/My Neighbor Totoro";
    private static final String LOG_TAG = TestPopMovieContract.class.getSimpleName();

    public void testBuildPopMovieTitle() {
        Uri uri = PopMoviesContract.PopMovieEntry.buildPopMoviesUri(1);
        Log.v(LOG_TAG, "uri with id = " + uri.toString());
        Uri movieUri = PopMoviesContract.PopMovieEntry.buildPopMoviesTitle(TEST_TITLE);
        Log.v(LOG_TAG, "movieUri = " + movieUri.toString());
        assertNotNull("a null Uri was returned from buildPopMoviesUri.", movieUri);
        assertEquals("Title not properly appended to end of URI", TEST_TITLE, movieUri.getLastPathSegment());
    }
}
