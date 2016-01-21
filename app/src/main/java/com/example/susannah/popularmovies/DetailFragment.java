/*
 * Copyright (C) 2016 S Medley
 */
package com.example.susannah.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    String mRating;
    String mReleaseDate;
    String mPosterPath;

    static final String KEY_TITLE = "TITLE";
    static final String KEY_ORIGTITLE = "ORIGTITLE";
    static final String KEY_SYNOPSIS = "SYNOPSIS";
    static final String KEY_RATING = "RATING";
    static final String KEY_RELEASEDATE = "RELEASEDATE";
    static final String KEY_POSTERPATH = "POSTERPATH";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(KEY_TITLE);
            mOriginalTitle = savedInstanceState.getString(KEY_ORIGTITLE);
            mSynopsis = savedInstanceState.getString(KEY_SYNOPSIS);
            mRating = savedInstanceState.getString(KEY_RATING);
            mReleaseDate = savedInstanceState.getString(KEY_RELEASEDATE);
            mPosterPath = savedInstanceState.getString(KEY_POSTERPATH);

        } else {

        }
        setHasOptionsMenu(true);
    }

    /** Displays the detailed information about one movie
     *
     * @param inflater The inflator used to inflate the layout
     * @param container The view group in which this view will reside
     * @param savedInstanceState The saved data to be displayed, if this view has already been created
     * @return The view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(layout.fragment_detail, container, false);

            Context context = getActivity().getApplicationContext();

            if (savedInstanceState == null) {
                Bundle extras = getActivity().getIntent().getExtras();
                if (extras == null) {
                    mTitle = "No data";
                } else {
                    mTitle = extras.getString(getString(string.title));
                    mOriginalTitle = extras.getString(getString(string.original_title));
                    if (mOriginalTitle == null)
                        mOriginalTitle = "";
                    if (mOriginalTitle.equals(mTitle))
                        mOriginalTitle = "";
                    mPosterPath = extras.getString(getString(string.poster_path));
                    mSynopsis = extras.getString(getString(string.synopsis));
                    mRating = extras.getString(getString(string.rating));
                    mReleaseDate = extras.getString(getString(string.release_date));
                }
            }
            ((TextView) root.findViewById(id.title)).setText(
                        String.format("%s: %s", context.getString(string.title),  mTitle));

            if (mOriginalTitle != null) {
                ((TextView) root.findViewById(id.original_title)).setText(mOriginalTitle);
            }

            ((TextView) root.findViewById(id.synopsis)).setText(mSynopsis);
            ((TextView) root.findViewById(id.rating)).setText(
                        String.format("%s: %s", context.getString(string.rating) , mRating));
            ((TextView) root.findViewById(id.release_date)).setText(
                        String.format("%s: %s", context.getString(string.release_date),mReleaseDate));
            ImageView thumbView = (ImageView) root.findViewById(id.movie_poster);

            // Use the movie database URI of the image and picasso to get the movie poster image to display.
            if (mPosterPath != null) {
                final String URI_SCHEME = "http";
                final String URI_AUTH = "image.tmdb.org";
                final String URI_T = "t";
                final String URI_P = "p";
                final String IMAGE_SIZE = "w342"; // a size, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using w185
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme(context.getString(R.string.uriScheme));
                uriBuilder.authority(context.getString(R.string.uriAuth));
                uriBuilder.appendPath(context.getString(R.string.uriT))
                        .appendPath(context.getString(R.string.uriP));
                uriBuilder.appendPath(IMAGE_SIZE);
                uriBuilder.appendPath(mPosterPath);
                String u = uriBuilder.build().toString();
                Picasso.with(context).load(u).into(thumbView);
            }
        }

        ((TextView) root.findViewById(id.title)).setText(mTitle);
        return root;
    }

    /** Save the data for 1 movie so the view can be recreated (for eg. this is a screen rotation)
     *
     * @param savedInstanceState the place to store the data
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_TITLE, mTitle);
        savedInstanceState.putString(KEY_ORIGTITLE, mOriginalTitle);
        savedInstanceState.putString(KEY_SYNOPSIS, mSynopsis);
        savedInstanceState.putString(KEY_RATING, mRating);
        savedInstanceState.putString(KEY_RELEASEDATE, mReleaseDate);
        savedInstanceState.putString(KEY_POSTERPATH, mPosterPath);
        super.onSaveInstanceState(savedInstanceState);
    }
}
