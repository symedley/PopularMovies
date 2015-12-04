package com.example.susannah.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;

    PopMovie[] popMovies = {
            new PopMovie("Matrix", R.drawable.thumb),
            new PopMovie("Inside Out", R.drawable.thumb)
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieGridAdapter = new MovieGridAdapter(getActivity(), Arrays.asList(popMovies));

        // Get a reference to the ListView, and attach this adapter to it.
            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(movieGridAdapter);

        return rootView;
    }
}
