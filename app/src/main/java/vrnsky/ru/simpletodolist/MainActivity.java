package vrnsky.ru.simpletodolist;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import vrnsky.ru.simpletodolist.adapter.TabAdapter;
import vrnsky.ru.simpletodolist.alarm.AlarmHelper;
import vrnsky.ru.simpletodolist.database.DBHelper;
import vrnsky.ru.simpletodolist.dialog.AddingTaskDialogFragment;
import vrnsky.ru.simpletodolist.dialog.EditTaskDialogFragment;
import vrnsky.ru.simpletodolist.fragments.CurrentTaskFragment;
import vrnsky.ru.simpletodolist.fragments.DoneTaskFragment;
import vrnsky.ru.simpletodolist.fragments.SplashFragment;
import vrnsky.ru.simpletodolist.fragments.TaskFragment;
import vrnsky.ru.simpletodolist.model.ModelTask;

/**
 * Main activity of app.
 */
public class MainActivity extends AppCompatActivity implements AddingTaskDialogFragment.AddingTaskListener, CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener, EditTaskDialogFragment.EditingTaskListener {

    FragmentManager fragmentManager;

    PreferenceHelper preferenceHelper;
    TabAdapter tabAdapter;

    TaskFragment currentTaskFragment;
    TaskFragment doneTaskFragment;

    public DBHelper dbHelper;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        Ads.showBanner(this);
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
        AlarmHelper.getInstance().init(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());

        fragmentManager = getFragmentManager();

        runSplash();

        setUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem splashItem = menu.findItem(R.id.action_splash);
        splashItem.setChecked(preferenceHelper.getBoolean(PreferenceHelper.SPLASH));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_splash) {
            item.setChecked(!item.isChecked());
            preferenceHelper.setBoolean(PreferenceHelper.SPLASH, item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runSplash() {
        if (!preferenceHelper.getBoolean(PreferenceHelper.SPLASH)) {
            SplashFragment splashFragment = new SplashFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, splashFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.currentTask));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.doneTask));

        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                try {
                                    if (object.has("name")) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_welcome,
                                                object.getString("name")), Toast.LENGTH_LONG).show();
                                        try {
                                            getSupportActionBar().setTitle(object.getString("name"));
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_error_get_user_info), Toast.LENGTH_LONG).show();
            }
        });
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new TabAdapter(fragmentManager, 2);

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        searchView = (SearchView) findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                currentTaskFragment.findTask(newText);
                doneTaskFragment.findTask(newText);
                return false;
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
                addingTaskDialogFragment.show(fragmentManager, "AddingTaskDialogFragment");
            }
        });

        FloatingActionButton fabFB = (FloatingActionButton) findViewById(R.id.fab_fb);
        fabFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<String> scopes = Arrays.asList("email");
//                SimpleAuth.getInstance().connectFacebook(scopes, new AuthCallback() {
//                    @Override
//                    public void onSuccess(SocialUser socialUser) {
//                        Log.d("qwerty", "userId:" + socialUser.userId);
//                        Log.d("qwerty", "email:" + socialUser.email);
//                        Log.d("qwerty", "accessToken:" + socialUser.accessToken);
//                        Log.d("qwerty", "first_name:" + socialUser.fullName);
//
//                        if (socialUser.userId != null && !socialUser.userId.equals("") && !socialUser.userId.equals("null")) {
//                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_welcome,
//                                    socialUser.fullName.replaceAll("null", "")), Toast.LENGTH_LONG).show();
//                            try {
//                                getSupportActionBar().setTitle(socialUser.fullName.replaceAll("null", ""));
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_error_get_user_info), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.d("qwerty", error.getMessage());
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d("qwerty", "Canceled");
//                    }
//                });

                loginButton.performClick();
            }
        });
    }

    @Override
    public void onTaskAdded(ModelTask newTask) {
        currentTaskFragment.addTask(newTask, true);
    }

    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this, getResources().getString(R.string.cancel_create_task), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTaskDone(ModelTask task) {
        doneTaskFragment.addTask(task, false);
    }

    @Override
    public void onTaskRestore(ModelTask task) {
        currentTaskFragment.addTask(task, false);
    }

    @Override
    public void onTaskEdited(ModelTask updateTask) {
        currentTaskFragment.updateTask(updateTask);
        dbHelper.update().task(updateTask);
    }
}
