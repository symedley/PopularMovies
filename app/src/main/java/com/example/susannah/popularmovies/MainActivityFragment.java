package com.example.susannah.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieListAdapter movieListAdapter;

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

        movieListAdapter = new MovieListAdapter(getActivity(), Arrays.asList(popMovies));

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_movies);
        listView.setAdapter(movieListAdapter);

        return rootView;
    }
}
