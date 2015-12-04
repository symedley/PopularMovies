package com.example.susannah.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Susannah on 11/29/2015.
 *
 * @param context   The current context. Used to inflate hte layout view.
 * @param movieList    A list of objects to display.
 */
public class MovieGridAdapter extends ArrayAdapter<PopMovie> {
    public MovieGridAdapter(Context context, List<PopMovie> movieList) {
        super(context, 0, movieList);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        PopMovie popMovie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView thumbView = (ImageView) convertView.findViewById(R.id.list_item_thumb);
        thumbView.setImageResource(popMovie.thumb);

        TextView movieListView = (TextView) convertView.findViewById(R.id.list_item_title);
        movieListView.setText(popMovie.title);

        return convertView;
    }
}
