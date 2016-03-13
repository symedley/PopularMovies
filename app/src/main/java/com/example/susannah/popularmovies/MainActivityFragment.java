package com.example.susannah.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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

/** MainActivityFragment is where most of the action happens. Holds the array of data and the grid adapter.
 *
 * Defines the data to display. Defines the adapter to hold the data to display
 * and sets the adapter to the GridView named in the .xml file.
 *
 * This class will have to create the call to the movie database and initialize a background
 * task to fetch and parse data.
 *
 * See MIN_VOTES in FetchMovieTask.doInBackground to adjust how many votes a movie must have to be included in the results.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Related to Content Provider, Loader and SQLiteDatabase
    private static final int CURSOR_LOADER_ID = 0;

    private boolean mSortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated

    // Changing to using SQLite and Content Provider
    // mPopMovieAdapter is the Cursor Adapter.
    // The old adapter was a grid adapter derived from ArrayAdapter.
    private PopMovieAdapter mPopMovieAdapter;
    private GridView mGridView;

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

    private String initializeSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sortOrd;
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if (sort.equals(getString(R.string.pref_sort_popular))) {
            mSortPopular = true;
            sortOrd = PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY + " DESC";
        }else if (sort.equals(getString(R.string.pref_sort_rated))) {
            mSortPopular = false;
            sortOrd = PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " DESC";
        } else {
            // this should not happen
            mSortPopular = false;
            sortOrd = PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " DESC";
            Log.e(LOG_TAG, "The sort order was not retrieved correctly from preferences");
        }
        return sortOrd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeSortOrder();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // START new SQLite and Provider
            // PopMovieAdapter created with activity, cursor, flags, loaderID
            mPopMovieAdapter = new PopMovieAdapter(getActivity(), null, 0);
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

                    // String selection = PopMoviesContract.PopMovieEntry._ID;
                    // String []  selectionArgs = new String[]{String.valueOf(position + 1)};

//                    Uri uri = PopMoviesContract.PopMovieEntry.buildPopMoviesUri(position + 1);
//                    Cursor c =
//                            getActivity().getContentResolver().query(
//                                    uri,
//                                    null,
//                                    null,
//                                    null,
//                                    null);
//
//                    if (c != null) {
////                        c.moveToFirst();
//
                        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                        detailIntent.putExtra(DetailFragment.KEY_POSITION, position + 1);

                        startActivity(detailIntent);
                    }
//                }
            });
        }
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        String sortOrder = initializeSortOrder();
        Cursor c =
                getActivity().getContentResolver().query(PopMoviesContract.PopMovieEntry.CONTENT_URI,
//                        new String[]{PopMoviesContract.PopMovieEntry._ID},
                        null,
                        null,
                        null,
                        sortOrder
                );

        if (c.getCount() == 0){
            updateMovies();
        }
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
        c.close();
    }

    /** updateMovies checks the sort order and then starts the fetchMovieTask
    * Sort order: user can choose to sort by Most Popular or by Highest Rated
    */
    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if (sort.equals(getString(R.string.pref_sort_popular)))
            mSortPopular = true;
        else if (sort.equals(getString(R.string.pref_sort_rated)))
            mSortPopular = false;
        else {
            // this should not happen
            Log.e(LOG_TAG, "Error retrieving sort order from preferences");
            mSortPopular = false;
        }
        FetchMovieTask fetchMovieTask = new FetchMovieTask(mSortPopular, getContext());
        fetchMovieTask.execute();

        FetchGenresTask fetchGenresTask = new FetchGenresTask( getContext());
        fetchGenresTask.execute();
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
    public void onDestroy() {
        super.onDestroy();
    }

    /** onCreateLoader - attach the query to our DB Loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = initializeSortOrder();
        return new CursorLoader(getActivity(),
                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }

    /** The data retrieval is finished, so let the client (this class) know
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPopMovieAdapter.swapCursor(data);
    }

    /** Data needs to be refreshed, so dump the old data
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader ) {
        mPopMovieAdapter.swapCursor(null);
    }

}
