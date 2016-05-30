package com.example.susannah.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.susannah.popularmovies.data.PopMoviesContract;

import java.util.ArrayList;
import java.util.List;

/**
 * ReviewArrayAdapter - display the array of reviews in a ListView
 *
 * Created by Susannah on 5/5/2016.
 */
class ReviewArrayAdapter extends ArrayAdapter {
    public ReviewArrayAdapter(Activity context, ArrayList<ReviewForOneMovie> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        ReviewForOneMovie review = (ReviewForOneMovie) getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }

        TextView reviewText = (TextView) convertView.findViewById(R.id.reviewText);
        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.author);

        reviewText.setText(review.review);
        reviewAuthor.setText(review.author);

        return convertView;
    }
}
