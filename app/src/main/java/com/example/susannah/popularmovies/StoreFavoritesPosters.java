package com.example.susannah.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.susannah.popularmovies.data.DbBitmapUtility;
import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * StoreFavoritesMovie Posters - Checks for all favorite movies or checks whether one movie is a
 * favorite and gets the bitmap poster if it is and stores it in the bitmaps table.
 *
 * Created by Susannah on 4/28/2016.
 */
class StoreFavoritesPosters extends AsyncTask<Integer, Void, Boolean> {

    private final String LOG_TAG = StoreFavoritesPosters.class.getSimpleName();
    private final Context mContext;

    public StoreFavoritesPosters(Context context) {
        mContext = context;
    }

    /**
     * doInBackground - save the bitmaps of fav movies into a special table.
     * <p/>
     * Get them from Picasso's cache. The PopMovieAdapter must have been initialized with
     * the data for this to work!
     */
    @Override
    protected Boolean doInBackground(Integer... params) {

        // get the favorite movies.
        // Check in movieImages table for the bitmaps of the posters corresponding to those TMDIDs.
        // If not found, look in Picasso to get cached bitmaps for the movieposterpath's of those movies.

        String selection = null;
        String selectionArgs[] = null;
        if (params.length > 0) {
            int targetTmdId = params[0];
            selection = PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "= ? ";
            selectionArgs = new String[] {String.valueOf(targetTmdId)};
        }
        Cursor favMoviesCursor;

        favMoviesCursor = mContext.getContentResolver().query(
                PopMoviesContract.FavoriteMovieEntry.buildAllFavoriteMoviesUri(),
                new String[] {PopMoviesContract.PopMovieEntry.COLUMN_TMDID, PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI}, // projection: only these two columns
                selection,
                selectionArgs,
                null);

        try {
            favMoviesCursor.moveToFirst();
            do {
                int idx = favMoviesCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
                final int tmdId = favMoviesCursor.getInt(idx);
                idx = favMoviesCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
                String posterPath = favMoviesCursor.getString(idx);

                Cursor imageEntry = mContext.getContentResolver().query(PopMoviesContract.MovieImages.CONTENT_URI,
                        null,
                        PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID + " =? ",
                        new String[]{String.valueOf(tmdId)},
                        null);
                if ((imageEntry == null) || (imageEntry.getCount() == 0)) {
                    // The image entry for this TmdId is missing, so try to get it and insert it in the database
                    try {
                        Bitmap imageBitmap = Picasso.with(mContext)
                                .load(posterPath)
                                .get();
                        // Now save an entry to the MovieImages table.
                        // gawd, this is convoluted.
                        ContentValues imageCvs = new ContentValues();
                        imageCvs.put(PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID, tmdId);
                        byte[] byteBitmap = DbBitmapUtility.getBytes(imageBitmap);
                        imageCvs.put(PopMoviesContract.MovieImages.COLUMN_IMAGE_DATA, byteBitmap);
                        Uri uri = mContext.getContentResolver().insert(
                                PopMoviesContract.MovieImages.CONTENT_URI,
                                imageCvs);
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "Picasso failed to give us the bitmap.");
                    }
                    if (imageEntry != null) {
                        imageEntry.close();
                    }
                }
            } while (favMoviesCursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException e) {
            Log.d(LOG_TAG, "Searching the images table failed: " + e.getMessage());
            return Boolean.FALSE;
        }catch (NullPointerException e) {
            Log.d(LOG_TAG, "Searching the images table failed: " + e.getMessage());
            return Boolean.FALSE;
        }
        finally {
            Log.v(LOG_TAG, "Finished checking for images in the database of the favorite movies.");
            favMoviesCursor.close();
        }
        return Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean success) {

    }
}
