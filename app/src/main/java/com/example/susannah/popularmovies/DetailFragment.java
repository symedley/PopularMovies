/*
 * Copyright (C) 2016 S Medley
 */
package com.example.susannah.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

import static com.example.susannah.popularmovies.R.*;

/**
 * Displays the details of a single movie, getting the data from extras.
 * <p/>
 * Created by Susannah on 12/28/2015.
 */
public class DetailFragment extends Fragment {

    View root;
    String mTitle;
    String mOriginalTitle;
    String mSynopsis;
    String mOverview;
    String mReleaseDate;
    int mTmdId;
    String mPosterPathUriString;


    static final String KEY_POSITION = "POSITION";

    static final String LOG_TAG = DetailFragment.class.getSimpleName();

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
        if (root == null) {
            root = inflater.inflate(layout.fragment_detail, container, false);

            Context context = getActivity().getApplicationContext();

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                mTitle = "No data";
            } else {
                int position = extras.getInt(KEY_POSITION);
                Uri uri = PopMoviesContract.PopMovieEntry.buildPopMoviesUri(position);
                Cursor c =
                        getActivity().getContentResolver().query(
                                uri,
                                null,
                                null,
                                null,
                                null);

                if (c != null) {
                    c.moveToFirst();

                    int idx;
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TITLE);
                    mTitle = c.getString(idx);
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_ORIGTITLE);
                    mOriginalTitle = c.getString(idx);
                    if (mOriginalTitle == null)
                        mOriginalTitle = "";
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
                    mPosterPathUriString = c.getString(idx);
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_OVERVIEW);
                    mSynopsis = c.getString(idx);
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_VOTEAVERAGE);
                    mOverview = c.getString(idx);
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_RELEASEDATE);
                    mReleaseDate = c.getString(idx);
                    idx = c.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
                    mTmdId = c.getInt(idx);
                    c.close();
                }
            }

            TextView tvOrigTitle = (TextView) root.findViewById(id.original_title);
            tvOrigTitle.setText(mOriginalTitle);
            if ((mOriginalTitle.isEmpty()) && !(mOriginalTitle.equals(mTitle))) {
                tvOrigTitle.setVisibility(View.VISIBLE);
            } else {
                tvOrigTitle.setVisibility(View.GONE);
            }

            ((TextView) root.findViewById(id.synopsis)).setText(mSynopsis);
            ((TextView) root.findViewById(id.rating)).setText(
                    String.format("%s: %s", context.getString(string.rating), mOverview));
            ((TextView) root.findViewById(id.release_date)).setText(
                    String.format("%s: %s", context.getString(string.release_date), mReleaseDate));
            ImageView thumbView = (ImageView) root.findViewById(id.movie_poster);

            // Use the movie database URI of the image and picasso to get the movie poster image to display.
            if (mPosterPathUriString != null) {
                Picasso.with(context).load(mPosterPathUriString).into(thumbView);
            }
            Uri uri = PopMoviesContract.MovieFavorites.buildMovieFavoritesUri(mTmdId);
            Cursor c =
                    getActivity().getContentResolver().query(
                            uri,
                            null,
                            null,
                            null,
                            null);
            ImageButton favButton= (ImageButton) root.findViewById(id.toggleFavoriteBtn);
            //TODO set click listener fopr favButton
           if (c != null) {
               if (c.moveToFirst()) {
//                   ((TextView) root.findViewById(id.favorite)).setText(string.FAVORITE);
                   Log.d(LOG_TAG, "This movie is a fav");
                   favButton.setSelected(Boolean.TRUE);
               } else {
                   Log.d(LOG_TAG, "This movie is NOT a fav: " + mTmdId);
                   favButton.setSelected(Boolean.FALSE);
               }
               c.close();
           }
        }

        ((TextView) root.findViewById(id.title)).setText(mTitle);
        return root;
    }
}
