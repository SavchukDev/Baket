package vrnsky.ru.simpletodolist.model;

import java.util.Date;

import vrnsky.ru.simpletodolist.R;

/**
 * Created by Egor on 25.10.2016.
 */
public class ModelTask implements Item {


    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;

    public static final String[] PRIORITY_LEVELS = {"Low Priority", "Normal Priority", "High Priority"};

    public static final int STATUS_OVERDUE = 0;
    public static final int STATUS_CURRENT = 1;
    public static final int STATUS_DONE = 2;

    private int priority;
    private int status;
    private String title;
    private long date;
    private long timeStamp;
    private int dateStatus;
    private String name;

    public ModelTask(String title, long date, int priority, int status, long timeStamp, String name) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.status = status;
        this.timeStamp = timeStamp;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ModelTask() {
        this.status = -1;
        this.timeStamp = new Date().getTime();
    }


    public boolean isTask() {
        return true;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    public int getPriorityColor() {
        switch(getPriority()) {
            case PRIORITY_HIGH:
                if(getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_high;
                } else {
                    return R.color.priority_high_selected;
                }
            case PRIORITY_NORMAL:
                if(getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_normal;
                } else {
                    return R.color.priority_normal_selected;
                }
            case PRIORITY_LOW:
                if(getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_low;
                } else {
                    return R.color.priority_low_selected;
                }
            default:
                return 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
