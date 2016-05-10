package com.example.susannah.popularmovies;

/**
 * Created by Susannah on 5/5/2016.
 */
public class ReviewForOneMovie {

    int id;
    String author;
    String review;
    String url;

    public ReviewForOneMovie(int _id, String _author, String _review, String _url){
        id = _id;
        author = _author;
        review = _review;
        url = _url;
    }
}
