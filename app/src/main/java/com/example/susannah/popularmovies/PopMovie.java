package com.example.susannah.popularmovies;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Susannah on 11/29/2015.
 * Represents 1 movie in the list array.
 */
public class PopMovie {

    int thumb; // integer reference to a thumbnail image
    String posterPath;
    String posterPathUri;

    boolean adult;
    String overview;
    String releaseDate;
    int genreIds[];
    int tmdId; // the movie ID, which I will need later
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
            int vTmdId,
            String vOrigTitle,
            String vOrigLang,
            String vTitle,
            String vBackdropPath,
            float vPopularity,
            int vVoteCount,
            boolean vVideo,
            float vVoteAverage
    ) {

        posterPath = vPosterPath.replaceFirst("/", "");

        // final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String URI_SCHEME = "http";
        final String URI_AUTH = "image.tmdb.org";
        final String URI_T = "t";
        final String URI_P = "p";
        // A ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
        final String IMAGE_SIZE = "w342";
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(URI_SCHEME);
        uriBuilder.authority(URI_AUTH);
        uriBuilder.appendPath(URI_T)
                .appendPath(URI_P);
        uriBuilder.appendPath(IMAGE_SIZE);
        uriBuilder.appendPath(posterPath);
        posterPathUri = uriBuilder.build().toString();

        adult = vAdult;
        overview = vOverview;
        releaseDate = vReleaseDate;
        genreIds = vGenreIds;
        tmdId = vTmdId;
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
            String vTitle) {

        title = vTitle;

        // TODO deal better with the dummy image
        thumb = R.drawable.thumb;
    }
}
