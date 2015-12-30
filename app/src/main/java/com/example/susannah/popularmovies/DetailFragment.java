package com.example.susannah.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras == null) {
                title =  "No data";
            } else {
                originalTitle = extras.getString(getString(R.string.original_title));
                posterPath = extras.getString(getString(R.string.poster_path));
                synopsis = extras.getString(getString(R.string.synopsis));
                rating = extras.getString(getString(R.string.rating));
                releaseDate = extras.getString(getString(R.string.release_date));
                ((TextView) root.findViewById(R.id.original_title)).setText(originalTitle);
                ((TextView) root.findViewById(R.id.synopsis)).setText(synopsis);
                ((TextView) root.findViewById(R.id.rating)).setText(rating);
                ((TextView) root.findViewById(R.id.release_date)).setText(releaseDate);
            }
        } else {
            title = "No data";
        }

        ((TextView) root.findViewById(R.id.title)).setText(title);
        return root;
    }

}
