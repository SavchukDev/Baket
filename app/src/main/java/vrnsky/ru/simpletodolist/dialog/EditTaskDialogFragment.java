package vrnsky.ru.simpletodolist.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import vrnsky.ru.simpletodolist.R;
import vrnsky.ru.simpletodolist.Utils;
import vrnsky.ru.simpletodolist.alarm.AlarmHelper;
import vrnsky.ru.simpletodolist.model.ModelTask;

/**
 * Created by Egor on 29.10.2016.
 */
public class EditTaskDialogFragment extends DialogFragment {

    public static EditTaskDialogFragment newInstance(ModelTask task) {
        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", task.getTitle());
        args.putLong("date", task.getDate());
        args.putInt("priority", task.getPriority());
        args.putLong("time_stamp", task.getTimeStamp());
        args.putString("name", task.getName());

        editTaskDialogFragment.setArguments(args);
        return editTaskDialogFragment;
    }

    private EditingTaskListener editingTaskListener;

    public interface EditingTaskListener {
        void onTaskEdited(ModelTask updateTask);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editingTaskListener = (EditingTaskListener)activity;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title");
        long date = args.getLong("date",0);
        int priority = args.getInt("priority",0);
        long timeStamp = args.getLong("time_stamp", 0);
        String name = args.getString("name");

        final ModelTask task = new ModelTask(title, date, priority, 0, timeStamp, name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_editing_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_task, null);

        final TextInputLayout tilName = (TextInputLayout)container.findViewById(R.id.tilDialogTaskName);
        final EditText etName = tilName.getEditText();
        tilName.setHint(getString(R.string.task_name));

        final TextInputLayout tilTitle = (TextInputLayout)container.findViewById(R.id.tilDialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();
        tilTitle.setHint(getString(R.string.task_title));

        TextInputLayout tilDate = (TextInputLayout)container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();
        tilDate.setHint(getString(R.string.task_date));

        TextInputLayout tilTime = (TextInputLayout)container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();
        tilTime.setHint(getString(R.string.task_time));
        builder.setView(container);

        Spinner spPriority = (Spinner)container.findViewById(R.id.spDialogTaskPriority);

        etTitle.setText(task.getTitle());
        etName.setText(task.getName());
        etTitle.setSelection(etTitle.length());
        if(task.getDate() != 0) {
            etDate.setText(Utils.getDate(task.getDate()));
            etTime.setText(Utils.getTime(task.getDate()));
        }

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);

        spPriority.setAdapter(priorityAdapter);
        spPriority.setSelection(task.getPriority());

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

        if(etDate.length() != 0 || etTime.length() != 0) {
            calendar.setTimeInMillis(task.getDate());
        }

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }
                DialogFragment datePicker = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                datePicker.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length() == 0) {
                    etTime.setText(" ");
                }
                DialogFragment timePickerFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };

                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });



        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                task.setName(etName.getText().toString());
                task.setStatus(ModelTask.STATUS_CURRENT);
                if(etDate.length() != 0 || etTime.length() != 0) {
                    task.setDate(calendar.getTimeInMillis());

                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                editingTaskListener.onTaskEdited(task);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etTitle.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilTitle.setError(getString(R.string.empty_task));
                }

                if (etName.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilName.setError(getString(R.string.empty_task_name));
                }

                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0 || etName.length() == 0) {
                            positiveButton.setEnabled(false);
                            if (s.length() == 0) {
                                tilTitle.setError(getString(R.string.empty_task));
                            } else {
                                tilTitle.setErrorEnabled(false);
                            }

                            if (etName.length() == 0) {
                                tilName.setError(getString(R.string.empty_task_name));
                            } else {
                                tilName.setErrorEnabled(false);
                            }
                        } else {
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                            tilName.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0 || etTitle.length() == 0) {
                            positiveButton.setEnabled(false);
                            if (s.length() == 0) {
                                tilName.setError(getString(R.string.empty_task_name));
                            } else {
                                tilName.setErrorEnabled(false);
                            }

                            if (etTitle.length() == 0) {
                                tilTitle.setError(getString(R.string.empty_task));
                            } else {
                                tilTitle.setErrorEnabled(false);
                            }
                        } else {
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                            tilName.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        return alertDialog;
    }
}
