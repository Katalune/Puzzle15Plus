package ua.pp.kata.puzzle15plus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import ua.pp.kata.puzzle15plus.game.GameFragment;

public class MainActivity extends Activity implements GameFragment.GameFragmentListener, LevelFragment.LevelFragmentListener{
    public static final String CUR_DIM = "current_dimension";
    public static final String MAX_DIM = "max_dimension";
    private static final String TITLE = "title";
    private String[] mTitles;
    private Integer[] mIcons = {
            R.drawable.ic_1_color,
            R.drawable.ic_2_color,
            R.drawable.ic_3_color,
            R.drawable.ic_5_color
    };
    private ActionBarDrawerToggle mDrawerToggle;
    private  ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        // find the retained fragment on activity restart
        RetainedFragment retainedFragment = (RetainedFragment) fm.findFragmentByTag(RetainedFragment.TAG);
        // create the retained fragment first time
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            fm.beginTransaction().add(retainedFragment, RetainedFragment.TAG).commit();
        }
        // load list of menu titles
        mTitles = this.getResources().getStringArray(R.array.titles);

        // Set up member variables
        mDrawerList = (ListView) findViewById(R.id.drawer_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up drawer's list view and click listener
        mDrawerList.setAdapter(new DrawerListAdapter(this, mTitles, mIcons));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable the "home" button in the corner of the action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // The ActionBarDrawerToggle implements DrawerLayout.DrawerListener, but it also facilitates
        // the proper interaction behavior between the action bar icon and the navigation drawer
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.drawable.ic_menu, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(getTitle());
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.app_name);
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Select item by default
        if (savedInstanceState == null) {
            selectItem(0);
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE));
        }
    }

    // Save current title
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE, getTitle());
    }

    /* Menu methods */

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mDrawerLayout.isDrawerVisible(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        mDrawerLayout.closeDrawer(mDrawerList);
        super.onOptionsMenuClosed(menu);
    }

    // We don't inflate the menu because additional items are not needed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons if any in this part
        return super.onOptionsItemSelected(item);
    }

    // GameFragmentListener methods
    @Override
    public void onStartGameButtonClick() {
        Fragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(GameFragment.STATE_INDEX, GameFragment.GAME_STATE); // start game
        fragment.setArguments(args);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.animation_come_in, R.animator.animation_come_out,
                        R.animator.animation_pop_in, R.animator.animation_pop_out)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPauseGameButtonClick() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        getFragmentManager().popBackStack();
    }

    // open start game screen after choosing new level
    @Override
    public void onLevelButtonClick() {
        selectItem(0);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    } 

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Update the main content by replacing fragment with
     * @param position selected position
     */
    private void selectItem(int position) {
        Fragment fragment = new Fragment();

        switch(position) {
            case 0:
                // Game
                fragment = new GameFragment();
                Bundle args = new Bundle();
                args.putInt(GameFragment.STATE_INDEX, GameFragment.START_STATE);
                fragment.setArguments(args);
                break;
            case 1:
                // Highscores
                fragment = new ScoreboardFragment();
                break;
            case 2:
                // Levels
                fragment = new LevelFragment();
                break;
            case 3:
                // We have one activity, so this will close the app.
                finish();
        }

        // switch the view
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.menu_come_in, R.animator.menu_come_out,
                        R.animator.menu_come_in, R.animator.menu_come_out)
                .replace(R.id.content_frame, fragment)
                .commit();

        // update selected item in the drawer
        mDrawerList.setItemChecked(position, true);
        // update actionbar title
        setTitle(mTitles[position]);
        // close the drawer
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
