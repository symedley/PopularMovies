package com.example.susannah.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

/** MainActivityFragment is where most of the action happens. Holds the array of data and the grid adapter.
 *
 * Defines the data to display. Defines the adapter to hold the data to display
 * and sets the adapter to the GridView named in the .xml file.
 *
 * This class will have to create the call to the movie database and initialize a background
 * task to fetch and parse data.
 *
 * See MIN_VOTES to adjust how many votes a movie must have to be included in the results.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private MovieGridAdapter movieGridAdapter;
    boolean sortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated

    PopMovie[] popMovieArray = {
            new PopMovie("Retrieving movie data...")
    };
    public ArrayList<PopMovie> popMovies;

    public static final String KEY_SAVED_INSTANCE_ARRAY = "KEY_SAVED_INSTANCE_ARRAY";

    public MainActivityFragment() {
        popMovies = new ArrayList(Arrays.asList(popMovieArray));
        sortPopular = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            popMovies = savedInstanceState.getParcelableArrayList(KEY_SAVED_INSTANCE_ARRAY);
        }
        setHasOptionsMenu(true);
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            if (savedInstanceState == null) {
                updateMovies();
            } else {
                // Pull the data out
            }
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

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
                    // or do I have to just pull the primitive objects out of oneMovie
                    // and pass them each individually to the Intent putExtra?
                    // Looks like I would have to have OneMovie implement Parcelable

                    detailIntent.putExtra(getString(R.string.title), oneMovie.mTitle);
                    detailIntent.putExtra(getString(R.string.original_title), oneMovie.mOrigTitle);
                    // Pass in only the poster image name instead of the whole Uri, so
                    // that the detail view can retrieve a larger image.
                    detailIntent.putExtra(getString(R.string.poster_path), oneMovie.posterPath);
                    detailIntent.putExtra(getString(R.string.synopsis), oneMovie.mOverview);
                    detailIntent.putExtra(getString(R.string.rating), Float.toString(oneMovie.mVoteAverage));
                    detailIntent.putExtra(getString(R.string.release_date), oneMovie.mReleaseDate);
                    startActivity(detailIntent);
                }
            });
        }
        return rootView;
    }

    /** updateMovies checks the sort order and then starts the fetchMovieTask
    * Sort order: user can choose to sort by Most Popular or by Highest Rated
    */
    private void updateMovies() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if (sort.equals(getString(R.string.pref_sort_popular)))
            sortPopular = true;
        else if (sort.equals(getString(R.string.pref_sort_rated)))
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

    /** Options Menu
     * The user has selected something from the menu.
     * Refresh
     * Sort: The sort order can be by most popular, or by highest-rated
     */
    public static final int RESULT_KEY = 7;
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
            startActivityForResult(settingsIntent, RESULT_KEY);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onActivityResult( int requestCode, int resultsCode, Intent data) {
        super.onActivityResult(requestCode, resultsCode, data);
        if (requestCode == RESULT_KEY) {
            updateMovies();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    /** onSaveInstanceState: so a screen rotation won't cause a new database query, do some magic or cheat.
     *
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putString(KEY_TITLE, mTitle);
        savedInstanceState.putParcelableArrayList(KEY_SAVED_INSTANCE_ARRAY, popMovies);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Boolean> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

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

                popMovies.add(oneMovie);
                //resultStrs[i] = title;
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
                if (sortPopular)
                    sortPref = POPULARITY_DESC;
                else
                    sortPref = RATED_DESC;

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
                movieGridAdapter.clear();
                movieGridAdapter.addAll(popMovies);
            }
        }
    }
}
