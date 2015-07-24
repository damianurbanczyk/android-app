package org.tndata.android.compass.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.MainViewPagerAdapter;
import org.tndata.android.compass.fragment.CategoryFragment.CategoryFragmentListener;
import org.tndata.android.compass.fragment.MyGoalsFragment.MyGoalsFragmentListener;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.DrawerItem;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.task.GetUserActionsTask.GetUserActionsListener;
import org.tndata.android.compass.task.GetUserBehaviorsTask;
import org.tndata.android.compass.task.GetUserBehaviorsTask.GetUserBehaviorsListener;
import org.tndata.android.compass.task.GetUserCategoriesTask;
import org.tndata.android.compass.task.GetUserCategoriesTask.GetUserCategoriesListener;
import org.tndata.android.compass.task.GetUserGoalsTask;
import org.tndata.android.compass.task.GetUserGoalsTask.GetUserGoalsListener;
import org.tndata.android.compass.task.UpdateProfileTask;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements
        GetUserCategoriesListener, GetUserGoalsListener, GetUserBehaviorsListener,
        GetUserActionsListener, MyGoalsFragmentListener, CategoryFragmentListener {
    private static final int IMPORTANT_TO_ME = 0;
    private static final int MY_PRIORITIES = 1;
    private static final int MYSELF = 2;
    private static final int MY_PRIVACY = 3;
    private static final int SETTINGS = 4;
    private static final int TOUR = 5;
    // NOTE: The Drawer menu option to launch the BehaviorProgressActivity is here for demo purposes.
    // We should remove before submitting to the play store.
    private static final int TEMP_MENU_FOR_BEHAVIOR_PROGRESS = 6;
    private static final int DRAWER_COUNT = 7;
    //private static final int DRAWER_COUNT = 6; // TODO: Remove the temporary menu item for Behavior Progress

    private CompassApplication application;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<DrawerItem> mDrawerItems;
    private DrawerAdapter mDrawerAdapter = null;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private ImageView mHeaderImageView;
    private MainViewPagerAdapter mAdapter;
    private boolean mDrawerIsOpen = false;
    private boolean backButtonSelectsDefaultTab = false;
    private static final int DEFAULT_TAB = 0;
    private boolean behaviorsLoadedDone = false;
    private boolean actionsLoadedDone = false;
    private boolean fetchedCategories = false; // Have we fetched the user's categories, already?
    // ^ this is used to prevent the app from hitting the api continuously after the app crashes;
    // when that happens, the CompassApplication loses its local values, then this activity
    // keeps calling showCategories in a loop, which hits the api without a proper auth token.

    @Override
    public void onBackPressed() {
        // This activity may switch tabs when a user taps a card, so after doing that,
        // we want the back button to return the user to the default tab.
        if(backButtonSelectsDefaultTab) {
            activateTab(DEFAULT_TAB);
            backButtonSelectsDefaultTab = false; // resets default behavior
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (CompassApplication) getApplication();

        //Update the timezone
        new UpdateProfileTask(null).execute(application.getUser());

        // Register the device with Google Cloud Messaging
        GcmRegistration gcm_registration = new GcmRegistration(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.transparent_tool_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.main_left_drawer_list);
        mDrawerItems = drawerItems();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerAdapter = new DrawerAdapter(this, mDrawerItems);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.nav_drawer_action, R.string.nav_drawer_action) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerIsOpen = false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerIsOpen = true;
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mHeaderImageView = (ImageView) findViewById(R.id.main_material_imageview);
        mAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(mAdapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.main_pager_tabstrip);
        tabs.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String url = mAdapter.getPositionImageUrl(position);
                if (url != null) {
                    ImageLoader.loadBitmap(mHeaderImageView, url, false, false);
                } else {
                    mHeaderImageView.setImageResource(R.drawable.path_header_image);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showCategories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.LOGGED_OUT_RESULT_CODE) {
            finish();
        } else if (requestCode == Constants.CHOOSE_CATEGORIES_REQUEST_CODE) {
            showCategories();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void chooseCategories() {
        Intent intent = new Intent(getApplicationContext(), ChooseCategoriesActivity.class);
        startActivityForResult(intent, Constants.CHOOSE_CATEGORIES_REQUEST_CODE);
    }

    public void showCategories() {
        ArrayList<Category> categories = application.getCategories();

        if (!fetchedCategories && (categories == null || categories.isEmpty())) {
            new GetUserCategoriesTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
        } else if(categories != null) {
            mAdapter.setCategories(application.getCategories());
            showGoals();

            mAdapter.notifyDataSetChanged();
        }
    }

    public void showGoals() {
        if (application.getGoals().isEmpty()) {
            new GetUserGoalsTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
        } else {
            Intent intent = new Intent(Constants.GOAL_UPDATED_BROADCAST_ACTION);
            sendBroadcast(intent);
        }
    }

    protected class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = null;
            switch (position) {
                case IMPORTANT_TO_ME:
                    break;
                case MY_PRIORITIES:
                    startActivity(new Intent(getApplicationContext(), MyPrioritiesActivity.class));
                    break;
                case MYSELF:
                    intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                    break;
                case MY_PRIVACY:
                    break;

                case SETTINGS:
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(intent, Constants.SETTINGS_REQUEST_CODE);
                    break;
                case TEMP_MENU_FOR_BEHAVIOR_PROGRESS:
                    intent = new Intent(getApplicationContext(), BehaviorProgressActivity.class);
                    startActivity(intent);
                    break;
                case TOUR:
//                    intent = new Intent(getApplicationContext(), .class);
//                    startActivity(intent);
                    break;
            }
            mDrawerLayout.closeDrawers();
        }
    }

    private ArrayList<DrawerItem> drawerItems() {
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        for (int i = 0; i < DRAWER_COUNT; i++) {
            DrawerItem item = new DrawerItem();
            switch (i) {
                case IMPORTANT_TO_ME:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.drawable = getResources().getDrawable(
                                R.drawable.ic_favorite_grey, null);
                    } else {
                        item.drawable = getResources().getDrawable(R.drawable.ic_favorite_grey);
                    }
                    item.text = getResources().getString(
                            R.string.action_important_to_me);
                    break;
                case MY_PRIORITIES:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.drawable = getResources().getDrawable(
                                R.drawable.ic_check_circle_grey, null);
                    } else {
                        item.drawable = getResources().getDrawable(R.drawable.ic_check_circle_grey);
                    }
                    item.text = getResources().getString(
                            R.string.action_my_priorities);
                    break;
                case MYSELF:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.drawable = getResources().getDrawable(
                                R.drawable.ic_face_grey, null);
                    } else {
                        item.drawable = getResources().getDrawable(R.drawable.ic_face_grey);
                    }
                    item.text = getResources().getString(R.string.action_myself);
                    break;
                case MY_PRIVACY:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.drawable = getResources().getDrawable(
                                R.drawable.ic_info_grey, null);
                    } else {
                        item.drawable = getResources().getDrawable(R.drawable.ic_info_grey);
                    }
                    item.text = getResources()
                            .getString(R.string.action_my_privacy);
                    break;
                case SETTINGS:
                    item.text = getResources().getString(R.string.action_settings);
                    break;
                case TEMP_MENU_FOR_BEHAVIOR_PROGRESS:
                    item.text = "Behavior Progress";
                    break;
                case TOUR:
                    item.text = getResources()
                            .getString(R.string.action_tour);
                    break;
            }
            items.add(item);
        }

        return items;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            if (mDrawerIsOpen) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void categoriesLoaded(ArrayList<Category> categories) {
        application.setCategories(categories);
        showCategories();
        fetchedCategories = true;
    }

    @Override
    public void goalsLoaded(ArrayList<Goal> goals) {
        application.setGoals(goals);
        new GetUserBehaviorsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
        new GetUserActionsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        if (behaviors != null) {
            // Save the user's selected behaviors
            application.setBehaviors(behaviors);
            behaviorsLoadedDone = true;
        }
        assignGoalsToCategories(false);
        showGoals();
        if(behaviorsLoadedDone && actionsLoadedDone) {
            application.assignActionsToBehaviors();
        }
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {
        application.setActions(actions);
        actionsLoadedDone = true;
        if(behaviorsLoadedDone && actionsLoadedDone) {
            application.assignActionsToBehaviors();
        }
    }

    @Override
    public void assignGoalsToCategories(boolean shouldSendBroadcast) {
        application.assignGoalsToCategories();
        if (shouldSendBroadcast) {
            Intent intent = new Intent(Constants.GOAL_UPDATED_BROADCAST_ACTION);
            sendBroadcast(intent);
            Log.d("Main Activity", "send broadcast");
        }
    }

    public void activateTab(int tabIndex) {
        mViewPager.setCurrentItem(tabIndex);
    }

    @Override
    public void transitionToCategoryTab(Category category) {
        backButtonSelectsDefaultTab = true;
        activateTab(mAdapter.getCategoryPosition(category) + 1);  // add one for the default tab
    }
}
