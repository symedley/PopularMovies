package com.example.susannah.popularmovies;

/**
 * Created by Susannah on 11/29/2015.
 * Represents 1 movie in the list array.
 */
public class PopMovie {
    String title;
    int thumb; // integer reference to a thumbnail image?
    public PopMovie(String vTitle, int vThumb){
        title = vTitle;
        thumb = vThumb;
    }
}
