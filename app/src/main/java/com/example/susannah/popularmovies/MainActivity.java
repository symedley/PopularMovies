package com.example.susannah.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.stetho.Stetho;

/** MainActivity is where it all starts.
 */
public class MainActivity extends AppCompatActivity {
    public static final String RIGHT_PANE_FRAGMENT = "RIGHT_PANE_FRAGMENT";
    private static String LOG_TAG = MainActivity.class.getSimpleName();
    /*
     * The ToolBar mTitle will be automatically set to the name of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean mIsTwoPane = false;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.right_panel) != null) {
            mIsTwoPane = true;
        }
        if (mIsTwoPane) {
            BlankFragment df = new BlankFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_panel,
                            df,
                            RIGHT_PANE_FRAGMENT).commit();
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
