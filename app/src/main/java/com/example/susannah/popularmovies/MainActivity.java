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
    private boolean mIsTwoPane;
    public static String RIGHT_PANE_FRAGMENT = "RIGHT_PANE_FRAGMENT";
    private static String LOG_TAG = MainActivity.class.getSimpleName();
    boolean justStarted = false;
    /*
     * The ToolBar mTitle will be automatically set to the name of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsTwoPane = false;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        justStarted = true;
        if (findViewById(R.id.right_panel) != null) {
            mIsTwoPane = true;
        }
        if (mIsTwoPane) {
//            View rightPanelView = inflate(R.layout.content_main_detail_panel, container, false);
            RelativeLayout rightPanelView = (RelativeLayout) findViewById(R.id.right_panel);
            mIsTwoPane = true;
            BlankFragment df = new BlankFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_panel,
                            df,
                            RIGHT_PANE_FRAGMENT).commit();

            Log.v(LOG_TAG, "this is a tablet screen!");
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
