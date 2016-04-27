/*
 * Copyright (C) 2016 S Medley
 */
package com.example.susannah.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.susannah.popularmovies.data.DbBitmapUtility;
import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

import static com.example.susannah.popularmovies.R.*;

/**
 * Displays the details of a single movie, getting the data from extras.
 * <p/>
 * Created by Susannah on 12/28/2015.
 */
public class DetailFragment extends Fragment {

    private View root;
    private long m_id;
    private String mTitle;
    private String mOriginalTitle;
    private String mSynopsis;
    private String mVoteAverage; // voteAverage is rating. The other sort criterion is Popularity
    private String mReleaseDate;
    private int mTmdId;
    private String mPosterPathUriString;
    private Boolean mIsFavorite;
    //private int mPosition;

    static final String KEY_TMDID = "TMDID";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Displays the detailed information about one movie
     *
     * @param inflater           The inflator used to inflate the layout
     * @param container          The view group in which this view will reside
     * @param savedInstanceState The saved data to be displayed, if this view has already been created
     * @return The view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Cursor movieCursor;

        if (root == null) {
            root = inflater.inflate(layout.fragment_detail, container, false);

            Context context = getActivity().getApplicationContext();

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                mTitle = "No data";
            } else {
                mTmdId = extras.getInt(KEY_TMDID);
                Uri uri = PopMoviesContract.PopMovieEntry.CONTENT_URI;
                movieCursor =
                        getActivity().getContentResolver().query(
                                uri,
                                null,
                                PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "=?",
                                new String[]{String.valueOf(mTmdId)},
                                null);

                if ((movieCursor == null) || (movieCursor.getCount() == 0)) {
                    // Not found in the popMovies table, so check the favorites table.
                    uri = PopMoviesContract.FavoriteMovieEntry.buildAllFavoriteMoviesUri();
                    movieCursor =
                            getActivity().getContentResolver().query(
                                    uri,
                                    null,
                                    PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "=?",
                                    new String[]{String.valueOf(mTmdId)},
                                    null);
                    // TODO if the cursor was not null but count was 0, do i need to close it before trying a new query?
                }
                if ((movieCursor != null) && (movieCursor.getCount() != 0)) {
                    movieCursor.moveToFirst();

                    int idx;
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TITLE);
                    mTitle = movieCursor.getString(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE);
                    mOriginalTitle = movieCursor.getString(idx);
                    if (mOriginalTitle == null)
                        mOriginalTitle = "";
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
                    mPosterPathUriString = movieCursor.getString(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW);
                    mSynopsis = movieCursor.getString(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE); // voteAverage == rating
                    mVoteAverage = movieCursor.getString(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE);
                    mReleaseDate = movieCursor.getString(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
                    mTmdId = movieCursor.getInt(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry._ID);
                    m_id = movieCursor.getInt(idx);
                    idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE);
                    mIsFavorite = (movieCursor.getInt(idx) == 1);
                    movieCursor.close();
                } else {
                    Log.d(LOG_TAG, "The movie with TmdId of " + mTmdId + " was not found in either table.");
                }
            }

            TextView tvOrigTitle = (TextView) root.findViewById(id.original_title);
            getActivity().setTitle(mOriginalTitle);
            // ((TextView) root.findViewById(id.title)).setText(mTitle);

            tvOrigTitle.setText(mOriginalTitle);
            if ((mOriginalTitle.isEmpty()) && !(mOriginalTitle.equals(mTitle))) {
                tvOrigTitle.setVisibility(View.VISIBLE);
            } else {
                tvOrigTitle.setVisibility(View.GONE);
            }


            ((TextView) root.findViewById(id.synopsis)).setText(mSynopsis);
            ((TextView) root.findViewById(id.rating)).setText( // voteAverage == rating
                    String.format("%s: %s", context.getString(string.rating), mVoteAverage));
            ((TextView) root.findViewById(id.release_date)).setText(
                    String.format("%s: %s", context.getString(string.release_date), mReleaseDate));
            ImageView thumbView = (ImageView) root.findViewById(id.movie_poster);
            // thumbView.setImageResource(R.drawable.thumb);

            // look in the images table that holds posters of the favorites
            Bitmap bm;
            Drawable errorImage = null;
            Cursor imageCursor = null;
            try {
                imageCursor = getActivity().getContentResolver().query(
                        PopMoviesContract.MovieImages.CONTENT_URI,
                        null,
                        PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID + " =? ",
                        new String[]{String.valueOf(mTmdId)},
                        null);
                int idx = imageCursor.getColumnIndex(PopMoviesContract.MovieImages.COLUMN_IMAGE_DATA);
                imageCursor.moveToFirst();
                byte[] bytes = imageCursor.getBlob(idx);
                bm = DbBitmapUtility.getImage(bytes);
                errorImage = new BitmapDrawable(getResources(), bm);
            } catch (CursorIndexOutOfBoundsException e) {
                // It's not in the database table. That's okay.
            } catch (NullPointerException e) {
                // The Bitmap could not be created because something along the chain went wrong.
            } catch (SQLException e) {
                // The Cursor doesn't have the format we expect. Probably image not found in table. That's okay.
            } finally {
                if (imageCursor != null) imageCursor.close();
            }

            if (errorImage == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    errorImage = getActivity().getResources().getDrawable(R.drawable.thumb_w342, getActivity().getTheme());
                } else {
                    errorImage = getActivity().getResources().getDrawable(R.drawable.thumb_w342);
                }
            }
            // Use the movie database URI of the image and picasso to get the movie poster image to display.
            if (mPosterPathUriString != null) {
                Picasso.with(context).load(mPosterPathUriString).placeholder(errorImage).error(errorImage).into(thumbView);
            }

            final ImageButton favButton = (ImageButton) root.findViewById(id.toggleFavoriteBtn);

            if (mIsFavorite) {
                Log.d(LOG_TAG, "This movie is a fav");
                favButton.setSelected(Boolean.TRUE);
            } else {
                Log.d(LOG_TAG, "This movie is NOT a fav: " + mTmdId);
                favButton.setSelected(Boolean.FALSE);
            }
            // User can toggle favorite status of this movie by clicking the button
            favButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Boolean clicked = favButton.isSelected();
                            Uri uri = PopMoviesContract.MovieFavoriteTmdId.buildMovieFavoritesIdUri(mTmdId);
                            Log.v(LOG_TAG, uri.toString());
                            if (clicked) {
                                // The user wants to un-set the favorites status
                                Log.d(LOG_TAG, "This movie is no longer a fav: " + mTmdId);
                                // delete from the list of favs table.
                                getActivity().getContentResolver().delete(uri, null, null);

                                // Set the boolean Favorite to false.
                                // To update just 1 column, add only that column to the content values
                                ContentValues cv = new ContentValues();
                                Uri movieUri = PopMoviesContract.PopMovieEntry.buildPopMoviesUriBy_Id(m_id);
                                cv.put(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE, 0);
                                int numUpdated = getActivity().getContentResolver().update(movieUri, cv, null, null);


                                // delete from the favoriteMovies table.
                                uri = PopMoviesContract.FavoriteMovieEntry.buildAllFavoriteMoviesUri();
                                int count = getActivity().getContentResolver().delete(uri,
                                        PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "=?",
                                        new String[]{String.valueOf(mTmdId)});

                                favButton.setSelected(Boolean.FALSE);


                            } else {
                                // User wants to make this a favorite.
                                // Set the boolean Favorite to true.
                                // To update just 1 column, add only that column to the content values
                                ContentValues cv = new ContentValues();
                                cv.put(PopMoviesContract.MovieFavoriteTmdId.COLUMN_MOVIE_TMDID, mTmdId);
                                Uri insert = getActivity().getContentResolver().insert(uri, cv);

                                // set the isFavorite column in popMovies table to TRUE
                                cv = new ContentValues();
                                Uri movieUri = PopMoviesContract.PopMovieEntry.buildPopMoviesUriBy_Id(m_id);
                                cv.put(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE, 1);
                                int numUpdated = getActivity().getContentResolver().update(movieUri, cv, null, null);

                                Cursor movieCursor = getActivity().getContentResolver().query(movieUri, null, null, null, null);
                                // Copy the movie to the favorites table where it will persist.
                                cv = cursorToContentValues(movieCursor);
                                movieUri = PopMoviesContract.FavoriteMovieEntry.buildAllFavoriteMoviesUri();
                                int count = getActivity().getContentResolver().delete(
                                        movieUri,
                                        PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "=?",
                                        new String[]{String.valueOf(mTmdId)});
                                uri = getActivity().getContentResolver().insert(movieUri, cv);

                                favButton.setSelected(Boolean.TRUE);
                            }
                        }
                    });

        }
        return root;
    }

    private ContentValues cursorToContentValues(Cursor movieCursor) {
        int idx;
        ContentValues retval = new ContentValues();
        String stringValue;
        int intVal;
        float floatVal;
        if (movieCursor.moveToFirst()) {
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry._ID);
            intVal = movieCursor.getInt(idx);
            retval.put(PopMoviesContract.PopMovieEntry._ID, intVal);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATH, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE); // voteAverage == rating
            floatVal = movieCursor.getFloat(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE, floatVal); // voteAverage == rating
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
            intVal = movieCursor.getInt(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_TMDID, intVal);

            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ADULT);
            intVal = movieCursor.getInt(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_ADULT, intVal);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_ORIGLANG, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TITLE);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_TITLE, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_BACKDROPPATH, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY);
            floatVal = movieCursor.getFloat(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_POPULARITY, floatVal);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT);
            intVal = movieCursor.getInt(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_VOTECOUNT, intVal);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO);
            stringValue = movieCursor.getString(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_VIDEO, stringValue);
            idx = movieCursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE);
            intVal = movieCursor.getInt(idx);
            retval.put(PopMoviesContract.PopMovieEntry.COLUMN_IS_FAVORITE, intVal);
        } else {
            retval = null;
        }
        return retval;
    }
}
