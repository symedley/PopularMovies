package com.example.susannah.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.susannah.popularmovies.data.PopMoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Susannah on 2/23/2016.
 */
public class PopMovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = PopMovieAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

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
//        sLoaderID = loaderID;
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
     * @param view
     * @param context
     * @param cursor      The data that was returned from the database and must now be displayed.
     */
    @Override
        public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // pull the data we need to display out of the cursor
        // the idx thing could be replaced by a "projection" into the database
        int idx = cursor.getColumnIndex(PopMoviesContract.PopMovieEntry.COLUMN_POSTERPATHURI);
        String posterPathUri = cursor.getString(idx);

        if (posterPathUri != null) {
            Picasso.with(context).load(posterPathUri).placeholder(R.drawable.thumb).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource( R.drawable.thumb );
        }
    }
}
