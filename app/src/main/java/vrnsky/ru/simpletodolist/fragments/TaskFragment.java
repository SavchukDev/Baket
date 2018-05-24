package vrnsky.ru.simpletodolist.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import vrnsky.ru.simpletodolist.MainActivity;
import vrnsky.ru.simpletodolist.R;
import vrnsky.ru.simpletodolist.adapter.CurrentTasksAdapter;
import vrnsky.ru.simpletodolist.adapter.TaskAdapter;
import vrnsky.ru.simpletodolist.alarm.AlarmHelper;
import vrnsky.ru.simpletodolist.dialog.EditTaskDialogFragment;
import vrnsky.ru.simpletodolist.model.Item;
import vrnsky.ru.simpletodolist.model.ModelTask;

/**
 * Created by Egor on 26.10.2016.
 */
public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter adapter;

    public MainActivity activity;

    public AlarmHelper alarmHelper;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }
        alarmHelper = AlarmHelper.getInstance();
        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void updateTask(ModelTask task) {
        adapter.updateTask(task);
    }

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);

    public abstract void findTask(String title);


    public abstract void checkAdapter();

    public void showTaskEditDialog(ModelTask task) {
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivity().getFragmentManager(), "EditiTaskDialogFragment");
    }

    public void removeTaskDialog(final int location) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.dialog_removing_message);
        Item item = adapter.getItem(location);
        if(item.isTask()) {
            ModelTask removingTask = (ModelTask)item;
            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator), R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_removing_message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if(isRemoved[0]) {
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removeTask(timeStamp);
                            }
                        }
                    });
                    snackbar.show();
                    dialog.dismiss();
                }
            });

            dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        }

        dialogBuilder.show();
    }
}
