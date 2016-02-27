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
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Susannah on 2/24/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Boolean> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private Boolean getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_POSTER_PATH = "poster_path";
        String posterPath;
        final String TMD_ADULT = "adult";
        boolean adult;
        final String TMD_OVERVIEW = "overview";
        String overview;
        final String TMD_RELEASE_DATE = "release_date";
        String releaseDate;
        final String TMD_GENRE_IDS = "genre_ids";       // An array of int. Usually between 1 and 5 entries

        final String TMD_ID = "id";                            // int
        int id;
        final String TMD_ORIGINAL_TITLE = "original_title";        // string
        String origTitle;
        final String TMD_ORIGINAL_LANGUAGE = "original_language";// 2 character string eg "en"
        String origLang;
        final String TMD_TITLE = "title";                    // string
        String title;
        final String TMD_BACKDROP_PATH = "backdrop_path";        // string which is a relative path
        String backdropPath;
        final String TMD_POPULARITY = "popularity";            // float
        float popularity;
        final String TMD_VOTE_COUNT = "vote_count";            // int
        int voteCount;
        final String TMD_VIDEO = "video";                    // boolean
        boolean video;
        final String TMD_VOTE_AVERAGE = "vote_average";        // float
        float voteAverage;

        ArrayList popMovies = new ArrayList();



        JSONObject forecastJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = forecastJson.getJSONArray(TMD_RESULTS);
       // String[] resultStrs = new String[movieArray.length()];
        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        Log.v(LOG_TAG, movieArray.length() + " movies were returned");
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject oneMovieJson = movieArray.getJSONObject(i);

            posterPath = oneMovieJson.getString(TMD_POSTER_PATH);
            adult = oneMovieJson.getBoolean(TMD_ADULT);
            overview = oneMovieJson.getString(TMD_OVERVIEW);
            releaseDate = oneMovieJson.getString(TMD_RELEASE_DATE);
            JSONArray genreIdArray = oneMovieJson.getJSONArray(TMD_GENRE_IDS);
            int[] genreIds = new int[genreIdArray.length()];
            for (int j = 0; j < genreIdArray.length(); j++) {
                genreIds[j] = genreIdArray.getInt(j);
            }
            id = oneMovieJson.getInt(TMD_ID);
            origTitle = oneMovieJson.getString(TMD_ORIGINAL_TITLE);
            origLang = oneMovieJson.getString(TMD_ORIGINAL_LANGUAGE);
            title = oneMovieJson.getString(TMD_TITLE);
            backdropPath = oneMovieJson.getString(TMD_BACKDROP_PATH);
            popularity = Float.parseFloat(oneMovieJson.getString(TMD_POPULARITY));
            voteCount = oneMovieJson.getInt(TMD_VOTE_COUNT);
            video = oneMovieJson.getBoolean(TMD_VIDEO);
            voteAverage = Float.parseFloat(oneMovieJson.getString(TMD_VOTE_AVERAGE));

            ContentValues movieValues = new ContentValues();

            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH, posterPath);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ADULT, (int) (adult? 1:0) );
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE, releaseDate);
           // movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_GENREIDS, genreIds);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TMDID, id);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE, origTitle);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG, origLang);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TITLE, title);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH, backdropPath);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT, voteCount);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO, (int) (video? 1:0));
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE, voteAverage);

            cVVector.add(movieValues);

            // TODO this is on its way out
            PopMovie oneMovie = new PopMovie(
                    posterPath,
                    adult,
                    overview,
                    releaseDate,
                    genreIds,
                    id,
                    origTitle,
                    origLang,
                    title,
                    backdropPath,
                    popularity,
                    voteCount,
                    video,
                    voteAverage);

            //TODO this is on its way out
            popMovies.add(oneMovie);
            //resultStrs[i] = title;
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            mContext.getContentResolver().bulkInsert(PopMoviesContract.PopMovieEntry.CONTENT_URI,
                    cVVector.toArray(new ContentValues[cVVector.size()]));
        }

        return Boolean.TRUE;
        //return resultStrs;
    }

    /*
     * Creates the URI and sends the message to the database.
     * Gets the resulting JSON and parses the data.
     * params might need to be something other than String. PErhaps should tell the
     * method whether to retrieve most popular or most highly rated.
     *
     */
    @Override
    protected Boolean doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        final String API_KEY_PARAM = "api_key";
        final String TOP_RATED = "top_rated";
        final String SORT_BY_PARAM = "sort_by";
        final String POPULARITY_DESC = "mPopularity.desc"; // sort by value is mPopularity descending
        final String RATED_DESC = "vote_average.desc"; // sort by value is mPopularity descending
        final String VOTE_COUNT = "vote_count.gte";
        final String MIN_VOTES = "100";
        //  add "vote_count.gte=x" so only movies with a lot of votes show up when doing vote average
        String sortPref;

        try {
            // Construct a URL for the query.
            // The API documentation is here: http://docs.themoviedb.apiary.io/#
            // but the documentation tool is almost unusable.
            final String BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
            final String BASE_URL_DISCOVER = "https://api.themoviedb.org/3/discover/movie";
            //TODO the sort should be moved to where data is pulled out
//            if (sortPopular)
//                sortPref = POPULARITY_DESC;
//            else
//                sortPref = RATED_DESC;
            sortPref = POPULARITY_DESC;

            Uri builtUri = Uri.parse(BASE_URL_DISCOVER).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sortPref)
                    .appendQueryParameter(VOTE_COUNT, MIN_VOTES)
                    .appendQueryParameter(API_KEY_PARAM, ApiKey.API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, " URL is " + url.toString());

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

            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
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
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if ((success != null) && (success == Boolean.TRUE)) {
//                movieGridAdapter.clear();
//                movieGridAdapter.addAll(popMovies);
        }
    }
}
