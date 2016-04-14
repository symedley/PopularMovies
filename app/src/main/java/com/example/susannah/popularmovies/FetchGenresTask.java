package com.example.susannah.popularmovies;

import android.content.ContentValues;
import android.content.Context;
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
import java.util.Vector;

/**
 * Created by Susannah on 3/9/2016.
 * When first launched, if there is no data in the table of movie genres,
 * fetch the list from themoviedb.org, parse it and put it in a DB table.
 */
public class FetchGenresTask extends AsyncTask<String, Void, Boolean> {

        private final String LOG_TAG = FetchGenresTask.class.getSimpleName();
        private final Context mContext;

        public FetchGenresTask(Context context) {
            mContext = context;
        }

        private Boolean getGenreDataFromJson(String genreJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMD_GENRES = "genres";
            final String TMD_GENRE_ID = "id";
            int genreId;
            final String TMD_GENRE_NAME = "name";
            String genreName;

            JSONObject forecastJson = new JSONObject(genreJsonStr);
            JSONArray genreArray = forecastJson.getJSONArray(TMD_GENRES);

            Vector<ContentValues> cVVector = new Vector<>(genreArray.length());

            Log.v(LOG_TAG, genreArray.length() + " genres were returned");
            for (int i = 0; i < genreArray.length(); i++) {
                JSONObject oneGenreJson = genreArray.getJSONObject(i);

                genreId = oneGenreJson.getInt(TMD_GENRE_ID);
                genreName = oneGenreJson.getString(TMD_GENRE_NAME);

                ContentValues genreValues = new ContentValues();

                genreValues.put(PopMoviesContract.GenreEntry.COLUMN_GENRE_ID, genreId);
                genreValues.put(PopMoviesContract.GenreEntry.COLUMN_NAME, genreName);

                cVVector.add(genreValues);
            }

            try {
                int delete = mContext.getContentResolver().delete(PopMoviesContract.GenreEntry.CONTENT_URI, null, null);
                // add to database
                if (cVVector.size() > 0) {
                    mContext.getContentResolver().bulkInsert(PopMoviesContract.GenreEntry.CONTENT_URI,
                            cVVector.toArray(new ContentValues[cVVector.size()]));
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception " + e.getMessage() + " " + e.toString());
            }

            return Boolean.TRUE;
        }

        /*
         * Creates the URI and sends the message to the database.
         * Gets the resulting JSON and parses the data.
         */
        @Override
        protected Boolean doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String genreJsonString = null;

            final String API_KEY_PARAM = "api_key";

            try {
                // Construct a URL for the query.
                // The API documentation is here: http://docs.themoviedb.apiary.io/#
                // but the documentation tool is almost unusable.
                final String BASE_URL_DISCOVER = "https://api.themoviedb.org/3/genre/movie/list";

                Uri builtUri = Uri.parse(BASE_URL_DISCOVER).buildUpon()
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty
                    return Boolean.FALSE;
                }

                genreJsonString = buffer.toString();
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
                return getGenreDataFromJson(genreJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if ((success != null) && (success == Boolean.TRUE)) {
                Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName()+" "+Thread.currentThread().getStackTrace()[2].getMethodName());
            }
        }
    }
