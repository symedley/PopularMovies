package com.example.susannah.popularmovies.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Susannah on 2/23/2016.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
   This helper function deletes all records from both database tables using the ContentProvider.
   It also queries the ContentProvider to make sure that the database has been successfully
   deleted.
 */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from PopMovie table during delete", 0, cursor.getCount());
        cursor.close();

        // Genre IDs table
        mContext.getContentResolver().delete(
                PopMoviesContract.GenreEntry.CONTENT_URI,
                null,
                null
        );

         cursor = mContext.getContentResolver().query(
                PopMoviesContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Genre table during delete", 0, cursor.getCount());
        cursor.close();
    }
    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the PopMoviesProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // PopMoviesProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PopMoviesProvider.class.getName());

        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: PopMoviesProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + PopMoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, PopMoviesContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: PopMoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
}
