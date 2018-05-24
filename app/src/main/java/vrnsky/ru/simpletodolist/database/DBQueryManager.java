package vrnsky.ru.simpletodolist.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vrnsky.ru.simpletodolist.model.ModelTask;

/**
 * Created by Vitaly on 24.08.2015.
 */
public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager(SQLiteDatabase database) {
        this.database = database;
    }

    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();

        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));
                String name = c.getString(c.getColumnIndex(DBHelper.TASK_NAME_COLUMN));

                ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp, name);
                tasks.add(modelTask);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
    }

    public ModelTask getTask(long timeStamp) {
        ModelTask task = null;
        Cursor cursor = database.query(DBHelper.TASKS_TABLE, null, DBHelper.SELECTION_TIME_STAMP, new String[]{Long.toString(timeStamp)}, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            int priority = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_NAME_COLUMN));
            task = new ModelTask(title, date, priority, status, timeStamp, name);
        }
        cursor.close();
        return task;
    }
}
