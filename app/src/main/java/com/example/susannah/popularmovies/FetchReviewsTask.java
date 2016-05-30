package com.example.susannah.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.susannah.popularmovies.data.PopMoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * FetchReviewsTask retrieves the reviews for movies. There maybe multiple reviews for 1
 * movie, and this task will be used to sometimes retrieve reviews for 1 movie and sometimes
 * for about 20. In the case when it's about 20, that's 20 separate calls to TheMovieDB server.
 * We want this done sequentially, rather than have 20 separate threads all talking to the
 * server at once.
 * <p/>
 * <p/>
 * Created by Susannah on 2/24/2016.
 */
class FetchReviewsTask extends AsyncTask<Void, Void, Boolean> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private int[] mTargetTmdIds;

    private final Context mContext;

    private final String REVIEWS = "reviews";
    private final String VIDEOS = "videos";

    public FetchReviewsTask(int[] tmdIds, Context context) {
        mTargetTmdIds = tmdIds;
        mContext = context;
    }

    /**
     * getReviewDataFromJson - after the data has been received as a JSON buffer, pick it apart and populate the database.
     * <p/>
     * Delete the old content in the database right before inserting the newly retrieved data
     *
     * @param movieVideosJsonStr - the data that came back from the server
     * @return success/failure
     * @throws JSONException
     */
    private Boolean getVideoDataFromJson(int theTmdId, String movieVideosJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        // final String TMD_ID = "id"; // we already know this because it was passed in.
        // final String VIDEO_ID = "id";       // int the ID of the video is not interesting.
        // String videoId;
        final String TMD_KEY = "key";
        String keyToYouTubeMovie;
        final String TMD_NAME = "name";
        String videoName;
        final String TMD_SITE = "site";
        String site;
        final String TMD_SIZE = "size";
        int size;
        final String TMD_TYPE = "type";
        String type;

        Boolean retval = Boolean.TRUE;

        JSONObject reviewJson = new JSONObject(movieVideosJsonStr);
        JSONArray videoArray = reviewJson.getJSONArray(TMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<>(videoArray.length());
        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject oneVideoJSON = videoArray.getJSONObject(i);

            // Never used: videoId = oneVideoJSON.getString(VIDEO_ID);
            keyToYouTubeMovie = oneVideoJSON.getString(TMD_KEY);
            videoName = oneVideoJSON.getString(TMD_NAME);
            site = oneVideoJSON.getString(TMD_SITE);
            size = oneVideoJSON.getInt(TMD_SIZE);
            type = oneVideoJSON.getString(TMD_TYPE);

            ContentValues videoValues = new ContentValues();

            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_TMDID, theTmdId);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_KEY, keyToYouTubeMovie);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_NAME, videoName);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_SITE, site);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_SIZE, size);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_TYPE, type);

            cVVector.add(videoValues);
        }
        try {
            // Delete the old content in the database right before inserting the newly retrieved data
            int count;
            mContext.getContentResolver().delete(
                    PopMoviesContract.VideoEntry.CONTENT_URI,
                    PopMoviesContract.VideoEntry.COLUMN_TMDID + "=?",
                    new String[]{String.valueOf(theTmdId)});
            // add to database
            if (cVVector.size() > 0) {
                count = mContext.getContentResolver().bulkInsert(PopMoviesContract.VideoEntry.CONTENT_URI,
                        cVVector.toArray(new ContentValues[cVVector.size()]));
                if (count != cVVector.size())
                    Log.d(LOG_TAG, "the number of videos added doesn't match the number we TRIED to add");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception " + e.getMessage() + " " + e.toString());
            retval = Boolean.FALSE;
        }
        return retval;
    }

    /**
     * getReviewDataFromJson - after the data has been received as a JSON buffer, pick it apart and populate the database.
     * <p/>
     * Delete the old content in the database right before inserting the newly retrieved data
     *
     * @param movieReviewJsonStr - the data that came back from the server
     * @return success/failure
     * @throws JSONException
     */
    private Boolean getReviewDataFromJson(int theTmdId, String movieReviewJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_ID = "id"; // we already know this because it was passed in.
        final String REVIEW_ID = "id";                            // int the ID of the review is not interesting.
        String reviewId;
        final String TMD_AUTHOR = "author";
        String reviewAuthor;
        final String TMD_CONTENT = "content";
        String reviewContent;
        final String TMD_URL = "url";
        String reviewURL;

        JSONObject reviewJson = new JSONObject(movieReviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(TMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<>(reviewArray.length());

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject oneReviewJson = reviewArray.getJSONObject(i);

            reviewId = oneReviewJson.getString(REVIEW_ID);
            reviewAuthor = oneReviewJson.getString(TMD_AUTHOR);
            reviewContent = oneReviewJson.getString(TMD_CONTENT);
            reviewURL = oneReviewJson.getString(TMD_URL);


            ContentValues reviewValues = new ContentValues();

            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_TMDID, theTmdId);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_AUTHOR, reviewAuthor);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_CONTENT, reviewContent);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_URL, reviewURL);

            cVVector.add(reviewValues);
        }

        try {
            // Delete the old content in the database right before inserting the newly retrieved data
            int count;
            mContext.getContentResolver().delete(
                    PopMoviesContract.ReviewEntry.CONTENT_URI,
                    PopMoviesContract.ReviewEntry.COLUMN_TMDID + "=?",
                    new String[]{String.valueOf(theTmdId)});
            // add to database
            if (cVVector.size() > 0) {
                count = mContext.getContentResolver().bulkInsert(PopMoviesContract.ReviewEntry.CONTENT_URI,
                        cVVector.toArray(new ContentValues[cVVector.size()]));
                if (count != cVVector.size())
                    Log.d(LOG_TAG, "the number of reviews added doesn't match the number we TRIED to add");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception " + e.getMessage() + " " + e.toString());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /*
     * Creates the URI and sends the message to the database.
     * Gets the resulting JSON and parses the data.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean retval = Boolean.TRUE;

        for (int tmdId : mTargetTmdIds) {
            // send the request to the cloud for the reviews of one movie in the list

            retval = getReviewsAndTrailersForOne(tmdId, REVIEWS);
            retval |= getReviewsAndTrailersForOne(tmdId, VIDEOS);

        }
        return retval;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success != null) {
            if (success == Boolean.TRUE) {
                Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                        " " + Thread.currentThread().getStackTrace()[2].getMethodName());
            } else { // oh no! false success!
                Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                        " " + Thread.currentThread().getStackTrace()[2].getMethodName() +
                        " Failed to retrieve the reviews!");
            }
        }
        // Now that the movie entries have been downloaded, start a new ASync task
        // to check that the bitmaps for posters of favorite movies are stored.
        StoreFavoritesPosters storeFavoritesPostersTask = new StoreFavoritesPosters(mContext);
        storeFavoritesPostersTask.execute();
    }

    private Boolean getReviewsAndTrailersForOne(int tmdId, String reviewsOrVideos) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        final String API_KEY_PARAM = "api_key";

        int successCount = 0;
        int failCount = 0;
        try {
            // Construct a URL for the query.
            // Example: http://api.themoviedb.org/3/movie/118340/reviews?api_key=<my key>
            final String BASE_URL = "https://api.themoviedb.org/3/movie";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(String.valueOf(tmdId))
                    .appendPath(reviewsOrVideos)
                    .appendQueryParameter(API_KEY_PARAM, ApiKey.API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return Boolean.FALSE;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty
                return Boolean.FALSE;
            }

            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "No data retrieved! Error: " + e.toString() + " " + e.getMessage(), e);
            // If the data was not successfully retrieved, no need to parse it.
            return Boolean.FALSE;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }

        try {
            if (reviewsOrVideos.matches(REVIEWS))
                if (getReviewDataFromJson(tmdId, moviesJsonStr))
                    successCount++;
                else
                    failCount++;
            else if (getVideoDataFromJson(tmdId, moviesJsonStr))
                successCount++;
            else
                failCount++;
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Success count for " + reviewsOrVideos + " stored is: "
                    + String.valueOf(successCount)
                    + ". Fail count for " + reviewsOrVideos + " stored is "
                    + String.valueOf(failCount));
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
