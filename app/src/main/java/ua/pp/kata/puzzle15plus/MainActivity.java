package ua.pp.kata.puzzle15plus;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import ua.pp.kata.puzzle15plus.game.GameActivity;

public class MainActivity extends AppCompatActivity implements GameFragment.GameFragmentListener, LevelFragment.LevelFragmentListener {
    public static final String CUR_DIM = "current_dimension";
    public static final String MAX_DIM = "max_dimension";
    private static final String TITLE = "title";
    private static final String HIDDEN = "is_toolbar_hidden";
    private DrawerLayout mDrawerLayout;
    private NavigationView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enable the "home" button in the corner of the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up member variables
        StorageUtils.loadData(this);
        drawerList = (NavigationView) findViewById(R.id.drawer_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up drawer's list view and click listener
        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectItem(item);
                return true;
            }
        });

        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (setTitle()) {
                    invalidateOptionsMenu();
                }
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (setTitle()) {
                    invalidateOptionsMenu();
                }
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Select item by default
        if (savedInstanceState == null) {// get menu from navigationView
            selectItem(R.id.nav_item_1);
        } else {
            if (!savedInstanceState.getBoolean(HIDDEN)) {
                setTitle(savedInstanceState.getCharSequence(TITLE));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save current title
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE, getTitle());
        outState.putBoolean(HIDDEN, false);
    }

    // GameFragmentListener methods
    @Override
    public void onStartGameButtonClick() {
        startActivity(new Intent(this, GameActivity.class));
    }

    // open start game screen after choosing new level
    @Override
    public void onLevelButtonClick() {
        selectItem(R.id.nav_item_1);
    }

    private boolean setTitle() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
            return true;
        }
        return false;
    }

    private void selectItem(@IdRes int id) {
        selectItem(drawerList.getMenu().findItem(id));
    }

    private void selectItem(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new Fragment();

        switch (item.getItemId()) {
            case R.id.nav_item_1:
                // Game
                fragment = new GameFragment();
                Bundle args = new Bundle();
                args.putInt(GameFragment.STATE_INDEX, GameFragment.START_STATE);
                fragment.setArguments(args);
                setTitle(getString(R.string.nav_1));
                break;
            case R.id.nav_item_2:
                // Highscores
                fragment = new ScoreboardFragment();
                setTitle(getString(R.string.nav_2));
                break;
            case R.id.nav_item_3:
                // Levels
                fragment = new LevelFragment();
                setTitle(getString(R.string.nav_3));
                break;
            case R.id.nav_item_4:
                // We have one activity, so this will close the app.
                finish();
        }

        // switch the view
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.menu_come_in, R.animator.menu_come_out,
                        R.animator.menu_come_in, R.animator.menu_come_out)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

}
