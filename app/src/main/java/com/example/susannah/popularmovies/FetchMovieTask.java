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

/** FetchMovieTask is an Async (background) task for retrieving data via internet from themoviedb.
 *
 * Created by Susannah on 2/24/2016.
 */
class FetchMovieTask extends AsyncTask<String, Void, Boolean> {

    // A size, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using w185
    private static final String IMAGE_SIZE = "w342";

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private final Boolean mSortPopular;

    public FetchMovieTask(Boolean sortPop, Context context) {
        mSortPopular = sortPop;
        Log.v(LOG_TAG, "mSortPopular: " + mSortPopular);
        mContext = context;
    }

    /**getMovieDataFromJson - after the data has been received as a JSON buffer, pick it apart and populate the database.
     *
     * Delete the old content in the database right before inserting the newly retrieved data
     *
     * @param movieJsonStr - the data that came back from the server
     * @return success/failure
     * @throws JSONException
     */
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
        int tmdId;
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

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());

        Vector<ContentValues> cVFavsVector = new Vector<>(movieArray.length());

        // Estimate an average of 3 genres per movie.
        Vector<ContentValues> movieToGenres = new Vector<>(3 * movieArray.length());

        // Retrieve the list of user favorites. Just get the TMD IDs out of the table of movie favorites.
        // As each movie comes in from the JSON data, check to see if it's in the favorites list
        // and set the boolean flag FAVORITE to true.
        Cursor favs =
                mContext.getContentResolver().query(PopMoviesContract.FavoriteMovieEntry.buildAllFavoriteMoviesUri(),
                        new String[] { PopMoviesContract.PopMovieEntry.COLUMN_TMDID }, // projection of just TmdId
                        null,
                        null,
                        null
                );
        ArrayList favorites = new ArrayList<>(6);
        if (favs != null) {
            while (favs.moveToNext()) {
                int ti = favs.getInt(0);
                favorites.add(ti);
            }
            favs.close();
        }

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
            tmdId = oneMovieJson.getInt(TMD_ID);
            origTitle = oneMovieJson.getString(TMD_ORIGINAL_TITLE);
            origLang = oneMovieJson.getString(TMD_ORIGINAL_LANGUAGE);
            title = oneMovieJson.getString(TMD_TITLE);
            backdropPath = oneMovieJson.getString(TMD_BACKDROP_PATH);
            popularity = Float.parseFloat(oneMovieJson.getString(TMD_POPULARITY));
            voteCount = oneMovieJson.getInt(TMD_VOTE_COUNT);
            video = oneMovieJson.getBoolean(TMD_VIDEO);
            voteAverage = Float.parseFloat(oneMovieJson.getString(TMD_VOTE_AVERAGE));

            ContentValues movieValues = new ContentValues();
//            ContentValues favoritesValues;

            String fullPosterPath = posterPath.replaceFirst("/", "");

            // final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String URI_SCHEME = "http";
            final String URI_AUTH = "image.tmdb.org";
            final String URI_T = "t";
            final String URI_P = "p";
            // A size, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using w185
            // final String IMAGE_SIZE = "w342";
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme(URI_SCHEME);
            uriBuilder.authority(URI_AUTH);
            uriBuilder.appendPath(URI_T)
                    .appendPath(URI_P);
            uriBuilder.appendPath(IMAGE_SIZE);
            uriBuilder.appendPath(fullPosterPath);
            fullPosterPath = uriBuilder.build().toString();

            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH, posterPath);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI, fullPosterPath);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ADULT, (adult ? 1 : 0));
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE, releaseDate);
            // Genres are done differently
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TMDID, tmdId);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE, origTitle);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG, origLang);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_TITLE, title);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH, backdropPath);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT, voteCount);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO,  (video ? 1 : 0));
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE, voteAverage);
            movieValues.put(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE, favorites.contains(tmdId) ? 1 : 0);

            // Genres are a special case
            for (int j=0; j<genreIdArray.length(); j++) {
                ContentValues genreValues = new ContentValues();
                genreValues.put(PopMoviesContract.MovieToGenresEntry.COLUMN_MOVIE_TMDID, tmdId);
                genreValues.put(PopMoviesContract.MovieToGenresEntry.COLUMN_GENRE_ID, genreIds[j]);
                movieToGenres.add(genreValues);
            }

            cVVector.add(movieValues);
            if (favorites.contains(tmdId)) {
                cVFavsVector.add(movieValues);
            }
        }

        try {
            // Delete the old content in the database right before inserting the newly retrieved data
            int count = mContext.getContentResolver().delete(PopMoviesContract.PopMovieEntry.CONTENT_URI, null, null);
            // add to database
            if (cVVector.size() > 0) {
                mContext.getContentResolver().bulkInsert(PopMoviesContract.PopMovieEntry.CONTENT_URI,
                        cVVector.toArray(new ContentValues[cVVector.size()]));
                if (count != cVVector.size())
                    Log.d(LOG_TAG, "the number of movies added doesn't match the number we TRIED to add");
            }
            if (cVFavsVector.size() > 0) {
                mContext.getContentResolver().bulkInsert(PopMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                        cVFavsVector.toArray(new ContentValues[cVFavsVector.size()]));

            }
            count = mContext.getContentResolver().delete(PopMoviesContract.MovieToGenresEntry.CONTENT_URI, null, null);
            if (movieToGenres.size() > 0) {
                 count = mContext.getContentResolver().bulkInsert(PopMoviesContract.MovieToGenresEntry.CONTENT_URI,
                        movieToGenres.toArray(new ContentValues[movieToGenres.size()]));
                if (count != movieToGenres.size())
                    Log.d(LOG_TAG, "the number of genres added doesn't match the number we TRIED to add");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception " + e.getMessage() + " " + e.toString());
            return Boolean.FALSE;
        }

        // Now that the movie entries have been downloaded, start a new ASync task
        // to check that the bitmaps for posters of favorite movies are stored.
        StoreFavoritesPosters storeFavoritesPostersTask = new StoreFavoritesPosters(mContext);
        storeFavoritesPostersTask.execute();
        return Boolean.TRUE;
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
        final String POPULARITY_DESC = "popularity.desc"; // sort by value is mPopularity descending
        final String RATED_DESC = "vote_average.desc"; // sort by value is vote average descending
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

            if (mSortPopular)
                sortPref = POPULARITY_DESC;
            else
                sortPref = RATED_DESC;

            Uri builtUri = Uri.parse(BASE_URL_DISCOVER).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sortPref)
                    .appendQueryParameter(VOTE_COUNT, MIN_VOTES)
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
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success != null) {
          if(success == Boolean.TRUE){
                Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                        " " + Thread.currentThread().getStackTrace()[2].getMethodName());
            }else{ // oh no! false success!
              Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                      " " + Thread.currentThread().getStackTrace()[2].getMethodName() +
                        " Failed to retrieve movies!");
            }
        }
    }
}
