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
 * Created by Susannah on 12/28/2015.
 */
public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String title = "Title";
        String originalTitle;
        String synopsis;
        String rating;
        String releaseDate;
        String posterPath;

        View root = inflater.inflate(layout.fragment_detail, container, false);

        //Context context = container.getContext();
        Context context = getActivity().getApplicationContext();

        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras == null) {
                title =  "No data";
            } else {
                title = extras.getString(getString(string.title));
                originalTitle = extras.getString(getString(string.original_title));
                if (originalTitle.equals(title))
                    originalTitle = null;
                posterPath = extras.getString(getString(string.poster_path));
                synopsis = extras.getString(getString(string.synopsis));
                rating = extras.getString(getString(string.rating));
                releaseDate = extras.getString(getString(string.release_date));
                ((TextView) root.findViewById(id.title)).setText(context.getString(string.title) +": " + title);
                if (originalTitle != null) {
                    ((TextView) root.findViewById(id.original_title)).setText(context.getString(string.original_title) + ": " + originalTitle);
                }
                else {
                    ((TextView) root.findViewById(id.original_title)).setText("");
                }
                ((TextView) root.findViewById(id.synopsis)).setText( synopsis);
                ((TextView) root.findViewById(id.rating)).setText(context.getString(string.rating) +": " + rating);
                ((TextView) root.findViewById(id.release_date)).setText(context.getString(string.release_date) +": " + releaseDate);

                ImageView thumbView = (ImageView) root.findViewById(id.movie_poster);
                if (context != null) {
                    final String URI_SCHEME = "http";
                    final String URI_AUTH = "image.tmdb.org";
                    final String URI_T = "t";
                    final String URI_P = "p";
                    final String IMAGE_SIZE = "w342"; // a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.scheme(context.getString(R.string.uriScheme));
                    uriBuilder.authority(context.getString(R.string.uriAuth));
                    uriBuilder.appendPath(context.getString(R.string.uriT))
                            .appendPath(context.getString(R.string.uriP));
                    uriBuilder.appendPath(IMAGE_SIZE);
                    uriBuilder.appendPath(posterPath);
                    String u = uriBuilder.build().toString();
                    Picasso.with(context).load( u ).into(thumbView);
                }
            }
        } else {
            title = "No data";
        }

        ((TextView) root.findViewById(id.title)).setText(title);
        return root;
    }

}
