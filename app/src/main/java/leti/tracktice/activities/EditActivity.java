package leti.tracktice.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import leti.tracktice.R;
import leti.tracktice.ads.CommentsAd;
import leti.tracktice.ads.EntriesAd;
import leti.tracktice.ads.TasksAd;
import leti.tracktice.ads.Helper;


public class EditActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener {


    private SharedPreferences sharedPref;
    private SimpleDateFormat format;
    private int edit_time;

    // UI elements
    private Button button_start;
    private Button button_end;
    private Button button_go;
    private EditText et_com;
    private EditText et_task;
    private TextView et_dur;
    private Spinner spinner_tasks;
    private Spinner spinner_comments;
    private Activity activity;

    // Data elements
    private String entry_start;
    private String entry_end;
    private String entry_task;
    private String entry_comment;
    private String entry_dur;
    private String entry_seqno;
    private String entry_durLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_edit);

        activity = EditActivity.this;
        PreferenceManager.setDefaultValues(EditActivity.this, R.xml.settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        entry_start = Helper.get_entryStart(activity);
        entry_end = Helper.get_entryEnd(activity);
        entry_task = Helper.get_entryTask(activity);
        entry_comment = Helper.get_entryCom(activity);
        entry_dur = Helper.get_entryDur(activity);
        entry_seqno = Helper.get_entrySeqno(activity);


        // EditText // TextView

        et_task = (EditText) findViewById(R.id.et_task);
        et_task.setText(entry_task);

        et_com = (EditText) findViewById(R.id.et_com);
        et_com.setText(entry_comment);

        et_dur =(TextView) findViewById(R.id.et_dur);

        // Buttons

        button_go = (Button) findViewById(R.id.button_go);
        button_start = (Button) findViewById(R.id.button_start);
        button_end = (Button) findViewById(R.id.button_end);

        if (!entry_start.isEmpty()) {
            button_start.setText(entry_start);
        }
        if (!entry_end.isEmpty()) {
            button_end.setText(entry_end);
        }

        setButtons();

        button_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                edit_time = 0;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "DatePickerDialog");

            }
        });

        button_end.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                edit_time = 1;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        TextView button_ok = (TextView) findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (entry_start.equals(getString(R.string.entry_hintStart)) || entry_end.equals(getString(R.string.entry_hintEnd))) {
                    Snackbar.make(et_com, getString(R.string.toast_edit), Snackbar.LENGTH_LONG).show();
                } else {
                    try {
                        EntriesAd db = new EntriesAd(EditActivity.this);
                        db.open();

                        entry_task = et_task.getText().toString().trim();
                        entry_comment = et_com.getText().toString().trim();
                        entry_start = button_start.getText().toString().trim();
                        entry_end = button_end.getText().toString().trim();

                        if (entry_seqno.isEmpty()) {
                            try {
                                if(db.isExist(entry_start)){
                                    Snackbar.make(et_com, getString(R.string.toast_newStart), Snackbar.LENGTH_LONG).show();
                                }else{
                                    db.insert(entry_task, entry_comment, entry_dur, entry_start, entry_end);
                                    finish();
                                }
                            } catch (Exception e) {
                                Log.w("time_tracker", "Error Package name not found ", e);
                                Snackbar snackbar = Snackbar
                                        .make(et_com, R.string.toast_error, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } else {
                            try {
                                db.update(Integer.parseInt(entry_seqno), entry_task, entry_comment, entry_dur, entry_start, entry_end);
                                finish();
                            } catch (Exception e) {
                                Log.w("time_tracker", "Error Package name not found ", e);
                                Snackbar snackbar = Snackbar
                                        .make(et_com, R.string.toast_error, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        TextView button_cancel = (TextView) findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Spinners

        spinner_tasks = (Spinner) findViewById(R.id.spinner_tasks);
        spinner_comments = (Spinner) findViewById(R.id.spinner_comments);
        loadSpinnerData_tasks();
        loadSpinnerData_comments();

        onNewIntent(getIntent());
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();

        if ("shortcut_newEntry".equals(action)) {
            Helper.put_entrySeqno(activity, "");
            Helper.put_entryDur(activity, "");
            button_start.setText(R.string.entry_hintStart);
            button_end.setText(R.string.entry_hintEnd);
            et_task.setText("");
            et_com.setText("");
            setButtons();
            setButtons();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        if (position > 0) {

            Spinner spinner = (Spinner) parent;
            if(spinner.getId() == R.id.spinner_tasks) {
                String label = parent.getItemAtPosition(position).toString();
                et_task.setText(label);
                setButtons();
            }
            else if(spinner.getId() == R.id.spinner_comments) {
                String label = parent.getItemAtPosition(position).toString();
                et_com.setText(label);
                setButtons();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtons();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int monthInt = monthOfYear +1;

        String monthString = monthInt < 10 ? "0"+monthInt : ""+monthInt;
        String dayString = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;

        sharedPref.edit().putString("entry_date", year + "-" + monthString + "-" + dayString).apply();

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                EditActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.show(getFragmentManager(), "DatePickerDialog");
        setButtons();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String date = sharedPref.getString("entry_date", "") + " " + hourString + ":" + minuteString;

        if (edit_time == 0) {
            button_start.setText(date);
        } else {
            button_end.setText(date);
        }
        sharedPref.edit().putString("entry_date", "").apply();
        setButtons();
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar
                .make(et_com, R.string.toast_save, Snackbar.LENGTH_LONG)
                .setAction(R.string.toast_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        snackbar.show();
    }

    public void setButtons () {

        String start = getString(R.string.entry_hintStart);
        String end = getString(R.string.entry_hintEnd);

        Helper.put_entryStart(activity, button_start.getText().toString());
        Helper.put_entryEnd(activity, button_end.getText().toString());
        Helper.put_entryTask(activity, et_task.getText().toString());
        Helper.put_entryCom(activity, et_com.getText().toString());

        entry_start = Helper.get_entryStart(activity);
        entry_end = Helper.get_entryEnd(activity);
        entry_task = Helper.get_entryTask(activity);
        entry_comment = Helper.get_entryCom(activity);

        button_start.setText(entry_start);
        button_end.setText(entry_end);
        et_task.setText(entry_task);
        et_com.setText(entry_comment);

        if (entry_start.equals(start) && entry_end.equals(end)) {
            button_go.setText(getString(R.string.entry_hintGo));
            button_go.setVisibility(View.VISIBLE);
            et_dur.setVisibility(View.INVISIBLE);
        } else if (!entry_start.equals(start) && entry_end.equals(end)) {
            button_go.setText(getString(R.string.entry_hintGo_2));
            button_go.setVisibility(View.VISIBLE);
            et_dur.setVisibility(View.INVISIBLE);
        } else if (entry_start.equals(start) && !entry_end.equals(end)) {
            button_go.setText(getString(R.string.entry_hintGo));
            button_go.setVisibility(View.VISIBLE);
        } else if (!entry_start.equals(start) && !entry_end.equals(end)) {
            button_go.setVisibility(View.INVISIBLE);
            et_dur.setVisibility(View.VISIBLE);

            try {
                Date d1 = format.parse(entry_start);
                Date d2 = format.parse(entry_end);
                //Comparing dates
                double difference = Math.abs(d2.getTime() - d1.getTime());
                double differenceDates = difference / (60 * 60 * 1000);

                if(d2.after(d1)){
                    BigDecimal bd = new BigDecimal(differenceDates);
                    String hour = bd.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString();
                    et_dur.setText(Helper.dur_long(activity, hour));
                    entry_dur = hour;
                } else {
                    Snackbar.make(et_com, getString(R.string.toast_newEnd), Snackbar.LENGTH_LONG).show();
                    button_go.setText(getString(R.string.entry_hintGo_2));
                    button_go.setVisibility(View.VISIBLE);
                    et_dur.setVisibility(View.INVISIBLE);
                    button_end.setText(getString(R.string.entry_hintEnd));
                    setButtons();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = format.format(Calendar.getInstance().getTime());

                SimpleDateFormat format_time = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String time = format_time.format(Calendar.getInstance().getTime());

                if (entry_start.equals(getString(R.string.entry_hintStart))) {
                    button_start.setText(date);
                    setButtons();

                    android.content.Intent iMain = new android.content.Intent(activity, EditActivity.class);
                    PendingIntent piMain = PendingIntent.getActivity(activity, 0, iMain, 0);

                    String title;
                    String bigText;

                    if (!entry_task.isEmpty()) {
                        title = getString(R.string.share_task) + " " + entry_task;
                    } else {
                        title = getString(R.string.share_task) + " " + getString(R.string.entry_not);
                    }

                    if (!entry_comment.isEmpty()) {
                        bigText = getString(R.string.share_com) + " " + entry_comment + " | " + time;
                    } else {
                        bigText = getString(R.string.share_com) + " " + getString(R.string.entry_not) + " | " + time;
                    }

                    Notification notification = new NotificationCompat.Builder(activity)
                            .setColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                            .setSmallIcon(R.drawable.timelapse)
                            .setContentTitle(title)
                            .setContentText(bigText)
                            .setContentIntent(piMain)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setVibrate(new long[0])
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);
                    sharedPref.edit().putBoolean("finish_app", true).apply();
                    finish();

                } else {
                    button_end.setText(date);
                    setButtons();
                }
            }
        });
    }

    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData_tasks() {
        // database handler
        TasksAd db_tasks = new TasksAd(EditActivity.this);
        db_tasks.open();

        // Spinner Drop down elements
        List<String> labels = db_tasks.getRecords();
        Collections.sort(labels, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.add("...");
        adapter.addAll(labels);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tasks.setAdapter(adapter);
        spinner_tasks.setOnItemSelectedListener(this);
    }

    private void loadSpinnerData_comments() {
        // database handler
        CommentsAd db_comments = new CommentsAd(EditActivity.this);
        db_comments.open();

        // Spinner Drop down elements
        List<String> labels = db_comments.getRecords();
        Collections.sort(labels, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.add("...");
        adapter.addAll(labels);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_comments.setAdapter(adapter);
        spinner_comments.setOnItemSelectedListener(this);
    }
}
