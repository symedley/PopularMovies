package com.example.susannah.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Susannah on 11/29/2015.
 *
 * @param context   The current context. Used to inflate hte layout view.
 * @param movieList    A list of objects to display.
 */
public class MovieGridAdapter extends ArrayAdapter<PopMovie> {
    public MovieGridAdapter(Context context, int layout, int layoutResId, ArrayList<PopMovie> movieList) {
        super(context, layout, layoutResId, movieList);
    }

    private static final String LOG_TAG = MovieGridAdapter.class.getSimpleName();

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

        String IMAGE_SIZE = "w185";
        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        Context context = getContext() ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        }
        ImageView thumbView = (ImageView) convertView.findViewById(R.id.list_item_thumb);

        if (popMovie.posterPath != null) {
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme(context.getString(R.string.uriScheme));
            uriBuilder.authority(context.getString(R.string.uriAuth));
            uriBuilder.appendPath(context.getString(R.string.uriT))
                    .appendPath(context.getString(R.string.uriP));
            uriBuilder.appendPath(IMAGE_SIZE);
            uriBuilder.appendPath(popMovie.posterPath);
            String u = uriBuilder.build().toString();
            Picasso.with(getContext()).load( u ).into(thumbView);
        } else  {
            thumbView.setImageResource(popMovie.thumb);
        }

        /*Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("test", “w185” ).  // that’s the image size
        .appendQueryParameter("", popMovie.posterPath)
                .build();*/

        TextView movieListView = (TextView) convertView.findViewById(R.id.list_item_title);
        movieListView.setText(popMovie.title);

        return convertView;
    }
}
