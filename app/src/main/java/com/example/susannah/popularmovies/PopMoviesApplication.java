package com.example.susannah.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import static com.facebook.stetho.Stetho.*;

/**
 * Created by Susannah on 2/27/2016.
 */
public class PopMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //initializeWithDefaults(this);
    }
}
