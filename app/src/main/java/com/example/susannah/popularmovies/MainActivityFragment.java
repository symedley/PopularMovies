package com.example.susannah.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Related to Content Provider, Loader and SQLiteDatabase
    private static final int CURSOR_LOADER_ID = 0;

    private MovieGridAdapter movieGridAdapter;
    boolean sortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated

    // Changing to using SQLite and Content Provider
    private PopMovieAdapter mPopMovieAdapter;
    private GridView mGridView;

    PopMovie[] popMovieArray = {
            new PopMovie("Retrieving movie data...")
    };
    public ArrayList<PopMovie> popMovies;

    public static final String KEY_SAVED_INSTANCE_ARRAY = "KEY_SAVED_INSTANCE_ARRAY";

    public MainActivityFragment() {
        popMovies = new ArrayList(Arrays.asList(popMovieArray));
        sortPopular = true;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO can this be moved to onCreateView?
        Cursor c =
                getActivity().getContentResolver().query(PopMoviesContract.PopMovieEntry.CONTENT_URI,
                        new String[]{PopMoviesContract.PopMovieEntry._ID},
                        null,
                        null,
                        null);
        if (c.getCount() == 0){
            updateMovies();
        }
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
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
                // can this logic be moved here from the onACtivityCreated method? TODO
            }
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // START new SQLite and Provider
            // PopMovieAdapter created with activity, cursor, flags, loaderID
            mPopMovieAdapter = new PopMovieAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
//   WAS         movieGridAdapter = new MovieGridAdapter(getActivity(), R.layout.fragment_main,
//                    R.id.gridView, popMovies);
            // initialize to the GridView in fragment_main.xml
            mGridView = (GridView) rootView.findViewById(R.id.gridView);
//   WAS         // Get a reference to the ListView, and attach this adapter to it.
//            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
            // set the GridView's adapter to be our CursorAdapter, PopMovieAdapter
            mGridView.setAdapter(mPopMovieAdapter);
// WAS            gridView.setAdapter(movieGridAdapter);

            // END

            // When user clicks on a movie, open an activity with detail about
            // that one movie.
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
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
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putString(KEY_TITLE, mTitle);
        savedInstanceState.putParcelableArrayList(KEY_SAVED_INSTANCE_ARRAY, popMovies);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPopMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader ) {
        mPopMovieAdapter.swapCursor(null);
    }

}
