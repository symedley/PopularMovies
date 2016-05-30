package com.example.susannah.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.susannah.popularmovies.data.DbBitmapUtility;
import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Display the movie data in the cursor as a grid.
 * <p/>
 * Created by Susannah on 2/23/2016.
 */
class PopMovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = PopMovieAdapter.class.getSimpleName();
    private final Context mContext;

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.list_item_thumb);
        }
    }

    /*
     *
     */
    public PopMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    /*
     *
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_movie;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /**
     * Binds an image to one "cell" of the grid adapter
     *
     * @param view    the part of the screen in which to bind
     * @param context the context
     * @param cursor  The data that was returned from the database and must now be displayed.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        // pull the data we need to display out of the cursor
        // the idx thing could be replaced by a "projection" into the database
        int idx = cursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
        String posterPathUri = cursor.getString(idx);


        // look in the images table that holds posters of the favorites
        // and retrieve the bitmap in case Picasso fails because the network is not available.
        idx = cursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_TMDID);
        int tmdId = cursor.getInt(idx);
        Bitmap bm;
        Drawable errorImage = null;
        Cursor imageCursor = null;
        try {
            imageCursor = mContext.getContentResolver().query(
                    PopMoviesContract.MovieImages.CONTENT_URI,
                    null,
                    PopMoviesContract.MovieImages.COLUMN_MOVIE_TMDID + " =? ",
                    new String[]{String.valueOf(tmdId)},
                    null);

            if (imageCursor.moveToFirst()) {
                idx = imageCursor.getColumnIndex(PopMoviesContract.MovieImages.COLUMN_IMAGE_DATA);
                byte[] bytes = imageCursor.getBlob(idx);
                bm = DbBitmapUtility.getImage(bytes);
                errorImage = new BitmapDrawable(mContext.getResources(), bm);
            }
        } catch (CursorIndexOutOfBoundsException | NullPointerException e) {
            // It's not in the database table. That's okay.
        } catch (SQLException e) {
            // The Cursor doesn't have the format we expect. Probably image not found in table. That's okay.

        } finally {
            if (imageCursor != null) imageCursor.close();
        }

        if (errorImage == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                errorImage = mContext.getResources().getDrawable(R.drawable.thumb_w342, mContext.getTheme());
            } else {
                errorImage = mContext.getResources().getDrawable(R.drawable.thumb_w342);
            }
        }

        if (posterPathUri != null) {
            Picasso.with(context).load(posterPathUri)
                    .placeholder(errorImage)
                    .error(errorImage)
                    .into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.thumb);
        }
    }
}
