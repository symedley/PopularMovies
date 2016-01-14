package com.example.susannah.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/** PopMovie holds the data for one retrieved movie. Created when parsing JSON from the movie database.
 *
 * Created by Susannah on 11/29/2015.
 */
public class PopMovie implements Parcelable {

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
    int thumb; // integer reference to a thumbnail image

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
        // A size, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using w185
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

    /** private creator called when creating the object from the parcel
     *
     * @param parcelIn
     * Read all the data from the parcel in the same order it was written.
     */
    private PopMovie( Parcel parcelIn){
        posterPath = parcelIn.readString();
        parcelIn.writeString(posterPathUri);
        int adultInt = parcelIn.readInt();
        adult = (adultInt==1) ? true : false;

        overview = parcelIn.readString();
        releaseDate = parcelIn.readString();

        genreIds = parcelIn.createIntArray();

        tmdId = parcelIn.readInt();
        origTitle = parcelIn.readString();
        origLang = parcelIn.readString();
        title = parcelIn.readString();
        backdropPath = parcelIn.readString();
        // LEFT OFF. TODO
        popularity = parcelIn.readFloat();
        voteCount = parcelIn.readInt();

        int videoInt = parcelIn.readInt();
        video = (videoInt==1) ? true : false;

        voteAverage = parcelIn.readFloat();
        thumb = parcelIn.readInt();
    }

    @Override
    /** describeContents gives you a bitmask (?) is not required for this project
     *
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /** writeToParcel
     *
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(posterPathUri);

        int adultInt = (adult) ? 1 : 0;
        dest.writeInt(adultInt);

        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeIntArray(genreIds);
        dest.writeInt(tmdId);
        dest.writeString( origTitle);
        dest.writeString( origLang);
        dest.writeString(title);
        dest.writeString(backdropPath);
        // LEFT OFF. TODO
        dest.writeFloat(popularity);
        dest.writeInt( voteCount);

        int videoInt = (video) ? 1 : 0;
        dest.writeInt(  videoInt);

        dest.writeFloat(voteAverage);
        dest.writeInt( thumb); // integer reference to a thumbnail image
    }

    /**
     * Parcelable.Creator is an interface that must be defined. It generates instances of PopMovie from a parcel.
     * It creates an object called CREATOR object which is of type PopMovie.
     * 2 required methods
     * newArray will not be useful because we're using an ArrayAdapter which requires a ListArray (?)
     */
    public final Parcelable.Creator<PopMovie> CREATOR = new Creator<PopMovie>() {
        @Override
        public PopMovie createFromParcel(Parcel source) {
            return new PopMovie(source);
        }

        @Override
        public PopMovie[] newArray(int size) {
            return new PopMovie[size];
        }
    };
}
