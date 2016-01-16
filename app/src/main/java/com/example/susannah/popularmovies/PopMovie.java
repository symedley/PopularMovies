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

    boolean mAdult;
    String mOverview;
    String mReleaseDate;
    int mGenreIds[];
    int mTmdId; // the movie ID, which I will need later
    String mOrigTitle;
    String mOrigLang;
    String mTitle;
    String mBackdropPath;
    float mPopularity;
    int mVoteCount;
    boolean mVideo;
    float mVoteAverage;
    int mThumb; // integer reference to a thumbnail image

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

        mAdult = vAdult;
        mOverview = vOverview;
        mReleaseDate = vReleaseDate;
        mGenreIds = vGenreIds;
        mTmdId = vTmdId;
        mOrigTitle = vOrigTitle;
        mOrigLang = vOrigLang;
        mTitle = vTitle;
        mBackdropPath = vBackdropPath;
        mPopularity = vPopularity;
        mVoteCount = vVoteCount;
        mVideo = vVideo;
        mVoteAverage = vVoteAverage;

        // TODO deal better with the dummy image
        mThumb = R.drawable.thumb;
    }

    public PopMovie(
            String vTitle) {

        mTitle = vTitle;

        // TODO deal better with the dummy image
        mThumb = R.drawable.thumb;
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
        mAdult = (adultInt==1) ? true : false;

        mOverview = parcelIn.readString();
        mReleaseDate = parcelIn.readString();

        mGenreIds = parcelIn.createIntArray();

        mTmdId = parcelIn.readInt();
        mOrigTitle = parcelIn.readString();
        mOrigLang = parcelIn.readString();
        mTitle = parcelIn.readString();
        mBackdropPath = parcelIn.readString();
        mPopularity = parcelIn.readFloat();
        mVoteCount = parcelIn.readInt();

        int videoInt = parcelIn.readInt();
        mVideo = (videoInt==1) ? true : false;

        mVoteAverage = parcelIn.readFloat();
        mThumb = parcelIn.readInt();
    }

    @Override
    /** describeContents gives you a bitmask (?) is not required for this project
     *
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /** Write the one movie's data, in sequence, to a parcel so it can be unpacked later
     *
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(posterPathUri);

        int adultInt = (mAdult) ? 1 : 0;
        dest.writeInt(adultInt);

        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeIntArray(mGenreIds);
        dest.writeInt(mTmdId);
        dest.writeString(mOrigTitle);
        dest.writeString(mOrigLang);
        dest.writeString(mTitle);
        dest.writeString(mBackdropPath);

        dest.writeFloat(mPopularity);
        dest.writeInt(mVoteCount);

        int videoInt = (mVideo) ? 1 : 0;
        dest.writeInt(  videoInt);

        dest.writeFloat(mVoteAverage);
        dest.writeInt(mThumb); // integer reference to a thumbnail image
    }

    /** Parcelable.Creator is an interface that must be defined. It generates instances of PopMovie from a parcel.
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
