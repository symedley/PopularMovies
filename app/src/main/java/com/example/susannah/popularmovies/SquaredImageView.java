package com.example.susannah.popularmovies;

/**
 * Created by Susannah on 12/17/2015.
 * I defined this based on Picasso's code in GitHub. It's so that the MoveiGridAdapter getView can
 * do this Picasso.with(getContext()).load("http://i.imgur.com/DvpvklR.png").into(view);
 * where view is a SquaredImageView. This Picasso method would not take any other View type, would it?
 *
 * Do I really need to create this?
 *
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** An image view which always remains square with respect to its width. */
final class SquaredImageView extends ImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
