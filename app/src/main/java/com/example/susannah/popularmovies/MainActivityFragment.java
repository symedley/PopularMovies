package com.example.susannah.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Toast;

import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

/**
 * MainActivityFragment is where most of the action happens. Holds the array of data and the grid adapter.
 * <p/>
 * Defines the data to display. Defines the adapter to hold the data to display
 * and sets the adapter to the GridView named in the .xml file.
 * <p/>
 * This class will have to create the call to the movie database and initialize a background
 * task to fetch and parse data.
 * <p/>
 * See MIN_VOTES in FetchMovieTask.doInBackground to adjust how many votes a movie must have to be included in the results.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Related to Content Provider, Loader and SQLiteDatabase
    private static final int CURSOR_LOADER_ID = 0;

    private static final String KEY_DONT_UPDATE_MOVIES = "KEY_DONT_UPDATE_MOVIES";

    private boolean mSortPopular; // sort the movies by Most Popular if true. Otherwise sort by Highest rated
    private String mSortOrder;
    private String mSortOrderTitle;
    private boolean mIsTwoPane;
    private DetailFragment detailFragmentOnRight;

    // Changing to using SQLite and Content Provider
    // mPopMovieAdapter is the Cursor Adapter.
    // The old adapter was a grid adapter derived from ArrayAdapter.
    private PopMovieAdapter mPopMovieAdapter;
    private GridView mGridView;


    private View rootView;

    private Cursor mCursor;

    public MainActivityFragment() {
        // popMovies = new ArrayList(Arrays.asList(popMovieArray));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                " " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        int dontUpdate = 0;
        if (savedInstanceState != null) {
            dontUpdate = savedInstanceState.getInt(KEY_DONT_UPDATE_MOVIES, 0);
            Log.v(LOG_TAG, "dontUpdate is " + dontUpdate);
        }
        if ((savedInstanceState == null) || dontUpdate == 0)
            updateMovies();
        setHasOptionsMenu(true);

        Picasso.with(getContext()).setIndicatorsEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    /**
     * Options Menu: The user has selected something from the menu.
     * Refresh
     * Sort: The sort order can be by most popular, or by highest-rated
     */
    private static final int RESULT_KEY = 7;

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

        String retValue = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if (mSortOrder == null) {
            mSortOrder = PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " DESC";
            mSortOrderTitle = getString(R.string.pref_sort_label_rated) + getString(R.string._movies);
        }
        if (retValue.equals(getString(R.string.pref_sort_popular))) {
            mSortPopular = true;
            mSortOrder = PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY + " DESC";
            mSortOrderTitle = getString(R.string.pref_sort_label_popular) + getString(R.string._movies);
            retValue = mSortOrder;
        } else if (retValue.equals(getString(R.string.pref_sort_rated))) {
            mSortPopular = false;
            mSortOrder = PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE + " DESC";
            mSortOrderTitle = getString(R.string.pref_sort_label_rated) + getString(R.string._movies);
            retValue = mSortOrder;
        } else if (retValue.equals(getString(R.string.pref_sort_favorites))) {
            Log.v(LOG_TAG, "retValue preference is favorites");
            mSortOrderTitle = getString(R.string.favorite) + getString(R.string._movies);
            // Now show only the favorites.

        } else {
            // this should not happen
            mSortPopular = false;
            Log.e(LOG_TAG, "The retValue order was not retrieved correctly from preferences");
        }
        return retValue;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                " " + Thread.currentThread().getStackTrace()[2].getMethodName());
        initializeSortOrder();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            // PopMovieAdapter created with activity, mCursor, flags, loaderID
            mPopMovieAdapter = new PopMovieAdapter(getActivity(), mCursor, 0);
            // initialize to the GridView in fragment_main.xml
            mGridView = (GridView) rootView.findViewById(R.id.gridView);
            // set the GridView's adapter to be our CursorAdapter, PopMovieAdapter
            mGridView.setAdapter(mPopMovieAdapter);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mIsTwoPane = false;
        Fragment rightSideFragment = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.RIGHT_PANE_FRAGMENT);
        if (rightSideFragment != null) {
            mIsTwoPane = true;
        }
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get the item clicked on and display it's information
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCursor.moveToPosition(position);

                int idx = mCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
                Integer tmdId = mCursor.getInt(idx);


                if (mIsTwoPane) {
                    detailFragmentOnRight = new DetailFragment();
                    Bundle args = new Bundle();
                    args.putInt(DetailFragment.KEY_TMDID, tmdId);
                    detailFragmentOnRight.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.right_panel,
                                    detailFragmentOnRight,
                                    MainActivity.RIGHT_PANE_FRAGMENT).commit();
                } else {
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    detailIntent.putExtra(DetailFragment.KEY_TMDID, tmdId);

                    startActivity(detailIntent);
                }
            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                " " + Thread.currentThread().getStackTrace()[2].getMethodName());
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        // Check the preferences for sort order, then query the content provider.
        String sortOrder = initializeSortOrder();
        // if (mCursor != null) mCursor.close();
        if (sortOrder.equals(getString(R.string.pref_sort_favorites))) {
            // This is the special case of displaying only favorites
            mCursor =
                    getActivity().getContentResolver().query(
                            PopMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            mSortOrder
                    );
        } else {
            mCursor =
                    getActivity().getContentResolver().query(
                            PopMoviesContract.PopMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            mSortOrder
                    );
        }
        if (mCursor != null) {
            mPopMovieAdapter.swapCursor(mCursor);
            if (mCursor.getCount() == 0) {
                updateMovies();
            }
        } else {
            updateMovies();
        }
        getActivity().setTitle(mSortOrderTitle);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * updateMovies checks the sort order and then starts the fetchMovieTask
     * Sort order: user can choose to sort by Most Popular or by Highest Rated
     */
    private void updateMovies() {
        // initializeSortOrder sets the boolean mSortPopular before starting the FetchMovieTask
        initializeSortOrder();

        FetchMovieTask fetchMovieTask = new FetchMovieTask(mSortPopular, getContext());
        fetchMovieTask.execute();

        FetchGenresTask fetchGenresTask = new FetchGenresTask(getContext());
        fetchGenresTask.execute();
    }

    /**
     * Make the data be refreshed after changing a setting.
     * Could this also be done with an OnDataChanged method in this class
     * and calling it from onResume in MainActivity?
     */
    public void onActivityResult(int requestCode, int resultsCode, Intent data) {
        super.onActivityResult(requestCode, resultsCode, data);
        if (requestCode == RESULT_KEY) {
            String sortOrder = initializeSortOrder();
            if (sortOrder.equals(getString(R.string.pref_sort_favorites))) {
                // get a new set of data from the Provider to display, but don't initiate
                // a network call to tmdb
                mCursor =
                        getActivity().getContentResolver().query(
                                PopMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                mSortOrder
                        );

            } else {
                updateMovies();
                // DO initiate a network call to tmdb. And reset the cursor so
                // that when the data comes in, it will all display
                mCursor =
                        getActivity().getContentResolver().query(
                                PopMoviesContract.PopMovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                mSortOrder
                        );
            }
            mPopMovieAdapter.swapCursor(mCursor);
            getActivity().setTitle(mSortOrderTitle);
            // Blank the detail panel, if there is one
            if (mIsTwoPane) {
                BlankFragment df = new BlankFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.right_panel,
                                df,
                                MainActivity.RIGHT_PANE_FRAGMENT).commit();
            }
        }
    }

    /**
     * Save the data for 1 movie so the view can be recreated (for eg. this is a screen rotation)
     *
     * @param savedInstanceState the place to store the data
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v(LOG_TAG, Thread.currentThread().getStackTrace()[2].getClassName() +
                " " + Thread.currentThread().getStackTrace()[2].getMethodName());
        savedInstanceState.putInt(KEY_DONT_UPDATE_MOVIES, 1);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * onCreateLoader - attach the query to our DB Loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, ".....onCreateLoader");
        String sortOrder = initializeSortOrder();
        if (sortOrder.equals(getString(R.string.pref_sort_favorites))) {
            return new CursorLoader(getActivity(),
                    PopMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    mSortOrder);
        } else {
            return new CursorLoader(getActivity(),
                    PopMoviesContract.PopMovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    mSortOrder);
        }
    }

    /**
     * The data retrieval is finished, so let the client (this class) know
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, ".....onLoadFinished");
        //mCursor.close();
        String sortOrder = initializeSortOrder();
        if (sortOrder.equals(getString(R.string.pref_sort_favorites))) {
            // get a new set of data from the Provider to display, but don't initiate
            // a network call to tmdb
            mCursor =
                    getActivity().getContentResolver().query(
                            PopMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            mSortOrder
                    );
            if (data.getCount() == 0) {
                CharSequence text = "No favorites were found. Please switch to a different sort order and choose some favorites.";
                Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        }else
       // if (!(sortOrder.equals(getString(R.string.pref_sort_favorites))))
        {
            mCursor =
                    getActivity().getContentResolver().query(
                            PopMoviesContract.PopMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            mSortOrder
                    );
            if (data.getCount() == 0) {
                // It's ok for there to be 0 movies if the sort preference is favorites.
                // But otherwise, there should be some data retrieved.
                CharSequence text = "No movies were retrieved. Network problem?";
                Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        }
        mPopMovieAdapter.swapCursor(mCursor);

    }

    /**
     * Data needs to be refreshed, so dump the old data
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPopMovieAdapter.swapCursor(null);
    }
}
