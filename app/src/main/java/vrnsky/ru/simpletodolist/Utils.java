package vrnsky.ru.simpletodolist;

import java.text.SimpleDateFormat;

/**
 * Created by Egor on 25.10.2016.
 */
public class Utils {

    public static String getDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static String getTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }

    public static String getFullDate(long dateTime) {
        return String.format("%s %s", getDate(dateTime), getTime(dateTime));
    }
}
