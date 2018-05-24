package vrnsky.ru.simpletodolist.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import vrnsky.ru.simpletodolist.model.ModelTask;

/**
 * Created by Egor on 29.10.2016.
 */
public class AlarmHelper {

    private static AlarmHelper instance;
    private Context context;
    private AlarmManager alarmManager;

    public static AlarmHelper getInstance() {
        if(instance == null) {
            instance = new AlarmHelper();
        }

        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(ModelTask task) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("name", task.getName());
        intent.putExtra("time_stamp", task.getTimeStamp());
        intent.putExtra("color", task.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int)task.getTimeStamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);


    }

    public void removeAlarm(long timeStamp) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)timeStamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}

