package com.example.susannah.popularmovies;

import android.app.Activity;
import android.content.ContentUris;
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

    // private MovieGridAdapter mMovieGridAdapter;

    boolean sortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated

    // Changing to using SQLite and Content Provider
    // mPopMovieAdapter is the Cursor Adapter.
    // The old adapter was a grid adapter derived from ArrayAdapter.
    private PopMovieAdapter mPopMovieAdapter;
    private GridView mGridView;

//    PopMovie[] popMovieArray = {
//            new PopMovie("Retrieving movie data...")
//    };
//    public ArrayList<PopMovie> popMovies;

    View rootView;

    public MainActivityFragment() {
        // popMovies = new ArrayList(Arrays.asList(popMovieArray));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            popMovies = savedInstanceState.getParcelableArrayList(KEY_SAVED_INSTANCE_ARRAY);
//        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    /** Options Menu: The user has selected something from the menu.
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // START new SQLite and Provider
            // PopMovieAdapter created with activity, cursor, flags, loaderID
            mPopMovieAdapter = new PopMovieAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
            // initialize to the GridView in fragment_main.xml
            mGridView = (GridView) rootView.findViewById(R.id.gridView);
            // set the GridView's adapter to be our CursorAdapter, PopMovieAdapter
            mGridView.setAdapter(mPopMovieAdapter);
            // END

            // When user clicks on a movie, open an activity with detail about
            // that one movie.
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                // get the item clicked on and display it's information
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String selection = PopMoviesContract.PopMovieEntry._ID;
                    String []  selectionArgs = new String[]{String.valueOf(position + 1)};

                    Uri uri = PopMoviesContract.PopMovieEntry.buildPopMoviesUri(position + 1);
                    Cursor c =
                            getActivity().getContentResolver().query(
                                    uri,
                                    null,
                                    null,
                                    null,
                                    null);

                    if (c != null) {
                        c.moveToFirst();

//                         idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
//                        String posterPathUri = c.getString(idx);
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ADULT);
//                        boolean adult = c.getInt(idx) == 1 ? true : false;
//
//
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
//                        int tmdId = c.getInt(idx);
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG);
//                        String origLang = c.getString(idx);
//
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH);
//                        String backdropPath = c.getString(idx);
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY);
//                        float popularity = c.getFloat(idx);
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT);
//                        int voteCount = c.getInt(idx);
//                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO);
//                        boolean video = c.getInt(idx) == 1 ? true : false;
//
//                        PopMovie oneMovie = new PopMovie(
//                                posterPath,
//                                adult,
//                                overview,
//                                releaseDate,
//                                new int[]{1},
//                                tmdId,
//                                origTitle,
//                                origLang,
//                                title,
//                                backdropPath,
//                                popularity,
//                                voteCount,
//                                video,
//                                voteAverage);


                        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);

                        int idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TITLE);
                        String title = c.getString(idx);
                        detailIntent.putExtra(getString(R.string.title), title);

                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE);
                        String origTitle = c.getString(idx);
                        detailIntent.putExtra(getString(R.string.original_title), origTitle);

                        // Pass in only the poster image name instead of the whole Uri, so
                        // that the detail view can retrieve a larger image.
                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
                        String posterPathUri = c.getString(idx);
                        detailIntent.putExtra(getString(R.string.poster_path_uri_string), posterPathUri);

                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW);
                        String overview = c.getString(idx);
                        detailIntent.putExtra(getString(R.string.synopsis), overview);

                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE);
                        float voteAverage = c.getFloat(idx);
                        detailIntent.putExtra(getString(R.string.rating), Float.toString(voteAverage));

                        idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE);
                        String releaseDate = c.getString(idx);
                        detailIntent.putExtra(getString(R.string.release_date), releaseDate);
                        startActivity(detailIntent);
                    }
                }
            });
        }
        return rootView;
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
        // TODO revisit this vvv. The bulk insert should handle notification,
        // so why is this necessary?
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    /** Make the data be refreshed after changing a setting.
     * TODO revisit whether this is needed.
     * TODO should this be replaced by making an OnDataChanged method in this class
     * and calling it from onResume in MainActivity?
     */
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
    /** onSaveInstanceState: so a screen rotation won't cause a new database query,
     * do some magic or cheat.
     */
    // TODO when using a content provider, how do i do this?
    // I might not need this method at all
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putString(KEY_TITLE, mTitle);
//        savedInstanceState.putParcelableArrayList(KEY_SAVED_INSTANCE_ARRAY, popMovies);
    }


    /** onCreateLoader - attach the query to our DB Loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    /** The data retrieval is finished, so let the client (this class) know
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName()+" "+Thread.currentThread().getStackTrace()[2].getMethodName());
        mPopMovieAdapter.swapCursor(data);
    }

    /** Data needs to be refreshed, so dump the old data
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader ) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName()+" "+Thread.currentThread().getStackTrace()[2].getMethodName());
        mPopMovieAdapter.swapCursor(null);
    }

}
