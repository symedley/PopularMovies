package com.example.susannah.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TrailerArrayAdapter - display the array links to trailer videos in a ListView
 *
 * Created by Susannah on 511
 */
public class TrailerArrayAdapter extends ArrayAdapter {
    public TrailerArrayAdapter(Activity context, ArrayList<ReviewForOneMovie> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final TrailerForOneMovie trailer = (TrailerForOneMovie) getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }

        TextView trailerName = (TextView) convertView.findViewById(R.id.trailerName);

        trailerName.setText(trailer.name);


        return convertView;
    }
}
