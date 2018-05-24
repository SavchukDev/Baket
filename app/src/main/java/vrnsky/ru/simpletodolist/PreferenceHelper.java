package vrnsky.ru.simpletodolist;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton. It uses to have access to shared preferences.
 */
public class PreferenceHelper {

    /**
     * Key which hold at the preferences.
     */
    public static final String SPLASH = "splash_is_invisble";

    /**
     * Instance of this class
     */
    private static PreferenceHelper instance;

    /**
     * Context of application.
     */
    private Context context;

    /**
     * Permanent storage.
     */
    private SharedPreferences preferences;

    /**
     * For singleton pattern.
     */
    private PreferenceHelper() {

    }

    /**
     * Return an already exist instance of this class or create new instance.
     * @return instance of this class.
     */
    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    /**
     * Getting shared preferences.
     * @param context of application.
     */
    public void init(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("prefences", Context.MODE_PRIVATE);
    }

    /**
     * Return value which accossiate with given key.
     * @param key for searching in preferences.
     * @return value from shared preferences.
     */
    public boolean getBoolean(String key) {
        return this.preferences.getBoolean(key, false);
    }

    /**
     * Put value at the given key.
     * @param key for determine some key.
     * @param value value which will put to the shared preferences.
     */
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
