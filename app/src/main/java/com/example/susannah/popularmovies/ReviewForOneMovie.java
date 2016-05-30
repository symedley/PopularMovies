package com.example.susannah.popularmovies;

/**
 * Data for a single movie review. Needed in an object so it can be used by a list adapter.
 * Created by Susannah on 5/5/2016.
 */
 class ReviewForOneMovie {

    private int id;
    String author;
    String review;
    private String url;

    public ReviewForOneMovie(int _id, String _author, String _review, String _url){
        id = _id;
        author = _author;
        review = _review;
        url = _url;
    }
}
