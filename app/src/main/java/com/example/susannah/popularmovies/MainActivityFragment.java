package com.example.susannah.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Defines the data to display. Defines the adapter to hold the data to display
 * and sets the adapter to the GridView named in the .xml file.
 * <p/>
 * This class will have to create the call to the movie database and initialize a background
 * task to fetch and parse data.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;

    PopMovie[] popMovieArray = {
            new PopMovie("Matrix", R.drawable.thumb),
            new PopMovie("Inside Out", R.drawable.thumb)
    };
    public List popMovies;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        popMovies = Arrays.asList(popMovieArray);
        movieGridAdapter = new MovieGridAdapter(getActivity(), R.id.gridView, popMovies);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(movieGridAdapter);

        return rootView;
    }

    private void updateMovies() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId;
        itemId = menuItem.getItemId();
        if (itemId == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private final String _PARAM1 = "top_rated";

        private String[] getMovieDataFromJson(String movieJsonStr)
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

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(TMD_RESULTS);
            String[] resultStrs = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject oneMovie = movieArray.getJSONObject(i);

                posterPath = oneMovie.getString(TMD_POSTER_PATH);
                adult = oneMovie.getBoolean(TMD_ADULT);
                overview = oneMovie.getString(TMD_OVERVIEW);
                releaseDate = oneMovie.getString(TMD_RELEASE_DATE);
                JSONArray genreIdArray = oneMovie.getJSONArray(TMD_GENRE_IDS);
                int[] genreIds = new int[genreIdArray.length()];
                for (int j = 0; j < genreIdArray.length(); j++) {
                    genreIds[j] = genreIdArray.getInt(j);
                }
                id = oneMovie.getInt(TMD_ID);
                origTitle = oneMovie.getString(TMD_ORIGINAL_TITLE);
                origLang = oneMovie.getString(TMD_ORIGINAL_LANGUAGE);
                title = oneMovie.getString(TMD_TITLE);
                backdropPath = oneMovie.getString(TMD_BACKDROP_PATH);
                popularity = Float.parseFloat(oneMovie.getString(TMD_POPULARITY));
                voteCount = oneMovie.getInt(TMD_VOTE_COUNT);
                video = oneMovie.getBoolean(TMD_VIDEO);
                voteAverage = Float.parseFloat(oneMovie.getString(TMD_VOTE_AVERAGE));

                // resultStrs is a placeholder until i figure out how to get the
                // full movie object back from the async task
                resultStrs[i] = title;
            }

            return resultStrs;
        }

        /*
         * Creates the URI and sends the message to the database.
         * Gets the resulting JSON and parses the data.
         * params might need to be something other than String. PErhaps should tell the
         * method whether to retrieve most popular or most highly rated.
         *
         * TODO does this ever not return null? Why?
         */
        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            final String API_KEY_PARAM = "api_key";

            try {
                // Construct a URL for the query.
                // The API documentation is here: http://docs.themoviedb.apiary.io/#
                // but the documentation tool is almost unusable.
                final String BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty
                    return null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error: ", e);
                // If the data was not successfully retrieved, no need to parse it.
                return null;
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                popMovies = new ArrayList<PopMovie>();
                for (int i = 0; i < Array.getLength(strings); i++) {
                    popMovies.add(new PopMovie(strings[i], R.drawable.thumb));
                }
                // popMovies = new ArrayList<PopMovie>(Arrays.asList(strings));
                movieGridAdapter.clear();
                movieGridAdapter.addAll(popMovies);
            }
        }

    }
}
