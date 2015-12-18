package com.example.susannah.popularmovies;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Susannah on 11/29/2015.
 * Represents 1 movie in the list array.
 */
public class PopMovie {

    int thumb; // integer reference to a thumbnail image
    String posterPath;
    boolean adult;
    String overview;
    String releaseDate;
    int genreIds[];
    String origTitle;
    String origLang;
    String title;
    String backdropPath;
    float popularity;
    int voteCount;
    boolean video;
    float voteAverage;

    public PopMovie(
            String vPosterPath,
            boolean vAdult,
            String vOverview,
            String vReleaseDate,
            int vGenreIds[],
            String vOrigTitle,
            String vOrigLang,
            String vTitle,
            String vBackdropPath,
            float vPopularity,
            int vVoteCount,
            boolean vVideo,
            float vVoteAverage
    ) {

        posterPath = vPosterPath.replaceFirst("/","");
        adult = vAdult;
        overview = vOverview;
        releaseDate = vReleaseDate;
        genreIds = vGenreIds;
        origTitle = vOrigTitle;
        origLang = vOrigLang;
        title = vTitle;
        backdropPath = vBackdropPath;
        popularity = vPopularity;
        voteCount = vVoteCount;
        video = vVideo;
        voteAverage = vVoteAverage;

        // TODO deal better with the dummy image
        thumb = R.drawable.thumb;

    }

    public PopMovie(
            String vTitle ) {

        title = vTitle;

        // TODO deal better with the dummy image
        thumb = R.drawable.thumb;
    }
}
