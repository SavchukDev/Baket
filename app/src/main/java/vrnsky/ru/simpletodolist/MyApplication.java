package vrnsky.ru.simpletodolist;

import android.app.Application;

/**
 * Created by Egor on 29.10.2016.
 */
public class MyApplication extends Application {

    public static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResume() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
