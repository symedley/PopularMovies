package com.example.susannah.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.Arrays;

/**
 * Defines the data to display. Defines the adapter to hold the data to display
 * and sets the adapter to the GridView named in the .xml file.
 * <p/>
 * This class will have to create the call to the movie database and initialize a background
 * task to fetch and parse data.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;
    boolean sortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated

    PopMovie[] popMovieArray = {
            new PopMovie("Retrieving movie data...")
    };
    public ArrayList<PopMovie> popMovies;

    public MainActivityFragment() {
        popMovies = new ArrayList(Arrays.asList(popMovieArray));
        sortPopular = true;
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

        movieGridAdapter = new MovieGridAdapter(getActivity(), R.layout.fragment_main,
                R.id.gridView, popMovies);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(movieGridAdapter);

        // When user clicks on a movie, open an activity with detail about
        // that one movie.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get the item clicked on and display it's information
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopMovie oneMovie = (PopMovie) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                // TODO Can I pass the object PopMovie to the intent as an extra?
                // or do I have to just pull the primitive objects out of oneMovie
                // and pass them each individually to the Intent putExtra?

                detailIntent.putExtra(getString(R.string.title), oneMovie.title);
                detailIntent.putExtra(getString(R.string.original_title), oneMovie.origTitle);
                // Pass in only the poster image name instead of the whole Uri, so
                // that the detail view can retrieve a larger image.
                detailIntent.putExtra(getString(R.string.poster_path), oneMovie.posterPath);
                detailIntent.putExtra(getString(R.string.synopsis), oneMovie.overview);
                detailIntent.putExtra(getString(R.string.rating), Float.toString(oneMovie.voteAverage));
                detailIntent.putExtra(getString(R.string.release_date), oneMovie.releaseDate);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    /*
    * Sort order: user can choose to sort by Most Popular or by Highest Rated
    */
    private void updateMovies() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if (sort.equals( getString(R.string.pref_sort_popular)))
            sortPopular = true;
        else  if (sort.equals(  getString(R.string.pref_sort_rated)))
            sortPopular = false;
        else
            // this should not happen
            sortPopular = false;
        fetchMovieTask.execute();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    /*
     * Options Menu
     * The user has selected something from the menu.
     * Refresh
     * Sort: The sort order can be by most popular, or by highest-rated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId;
        itemId = menuItem.getItemId();
        if (itemId == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        if (itemId == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
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

            // first clear out the existing array
            // TODO replace this with directly adding to the ArrayList so that I can avoid the magic number
            popMovies = new ArrayList();

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(TMD_RESULTS);
            String[] resultStrs = new String[movieArray.length()];
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

                // resultStrs is a placeholder until i figure out how to get the
                // full movie object back from the async task
                popMovies.add(oneMovie);
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
            final String TOP_RATED = "top_rated";
            final String SORT_BY_PARAM = "sort_by";
            final String POPULARITY_DESC = "popularity.desc"; // sort by value is popularity descending
            final String RATED_DESC = "vote_average.desc"; // sort by value is popularity descending
            String sortPref;

            try {
                // Construct a URL for the query.
                // The API documentation is here: http://docs.themoviedb.apiary.io/#
                // but the documentation tool is almost unusable.
                final String BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
                final String BASE_URL_DISCOVER = "https://api.themoviedb.org/3/discover/movie";
                if (sortPopular)
                    sortPref = POPULARITY_DESC;
                else
                    sortPref = RATED_DESC;

                Uri builtUri = Uri.parse(BASE_URL_DISCOVER).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortPref)
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
            } catch ( IOException e ) {
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
            } catch (JSONException e ) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                //popMovies = new ArrayList( Arrays.asList(popMovieArray));
                movieGridAdapter.clear();
                movieGridAdapter.addAll(popMovies);
            }
        }
    }
}
