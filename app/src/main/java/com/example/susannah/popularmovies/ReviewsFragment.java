package com.example.susannah.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.susannah.popularmovies.data.PopMoviesContract;

import java.util.ArrayList;

/**
 * ReviewsFragment - The user will open this activity to read reviews. Linked to from the Details view.
 * Created by Susannah on 5/8/2016.
 */
public class ReviewsFragment extends Fragment {
    static final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    static final String KEY_TMDID = "TMDID";
    static final String KEY_TITLE = "TITLE";
    private int mTmdId;
    private String mTitle;
    private View root;

    public ReviewsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_reviews, container, false);

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                Log.e(LOG_TAG, " Received no data!");
            } else {
                mTmdId = extras.getInt(KEY_TMDID);
                mTitle = extras.getString(KEY_TITLE);
                getActivity().setTitle("Reviews for " + mTitle);
                Cursor reviewsCursor =
                        getActivity().getContentResolver().query(
                                PopMoviesContract.ReviewEntry.buildReviewsAll(),
                                null,
                                PopMoviesContract.PopMovieEntry.COLUMN_TMDID + "=?",
                                new String[]{String.valueOf(mTmdId)},
                                null);

                ArrayList reviews = new ArrayList<>();
                if ((reviewsCursor != null) && reviewsCursor.moveToFirst()) {
                    reviewsCursor.moveToPrevious();
                    while (reviewsCursor.moveToNext()) {
                        int idx = reviewsCursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_AUTHOR);
                        String auth = reviewsCursor.getString(idx);
                        idx = reviewsCursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_CONTENT);
                        String content = reviewsCursor.getString(idx);
                        idx = reviewsCursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_TMDID);
                        int tmdid = reviewsCursor.getInt(idx);
                        idx = reviewsCursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_URL);
                        String url = reviewsCursor.getString(idx);
                        ReviewForOneMovie oneReview = new ReviewForOneMovie(tmdid, auth, content, url);

                        reviews.add(oneReview);
                    }
                    reviewsCursor.close();
                }

                ReviewArrayAdapter reviewArrayAdapter = new ReviewArrayAdapter(getActivity(), reviews);
                ListView reviewsList = ((ListView) root.findViewById(R.id.listview_reviews));
                reviewsList.setAdapter(reviewArrayAdapter);
            }
        }
        return root;
    }
}
