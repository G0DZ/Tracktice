package leti.tracktice.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import leti.tracktice.R;
import leti.tracktice.ads.EntriesAd;
import leti.tracktice.ads.Helper;

public class MainActivity extends AppCompatActivity {

    //calling variables
    private EntriesAd db;
    private SimpleCursorAdapter adapter;
    private ListView lv = null;
    private EditText filter;
    private SharedPreferences sharedPref;
    private RelativeLayout filter_layout;
    private String total;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = MainActivity.this;

        PreferenceManager.setDefaultValues(activity, R.xml.settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putBoolean("finish_app", false).apply();
        sharedPref.edit().putBoolean("recreate_app", false).apply();

        setTitle(getString(R.string.app_name));

        filter_layout = (RelativeLayout) findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) findViewById(R.id.listNotes);
        filter = (EditText) findViewById(R.id.myFilter);

        ImageButton ib_hideKeyboard =(ImageButton) findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter.getText().length() > 0) {
                    filter.setText("");
                } else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    filter_layout.setVisibility(View.GONE);
                    setNotesList();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.put_entrySeqno(activity, "");
                Helper.put_entryStart(activity, "");
                Helper.put_entryEnd(activity, "");
                Helper.put_entryTask(activity, "");
                Helper.put_entryCom(activity, "");
                Helper.put_entryDur(activity, "");
                Helper.switchToActivity(activity, EditActivity.class);
            }
        });

        //calling EntriesAd
        db = new EntriesAd(activity);
        db.open();

        setNotesList();
    }

    private void setNotesList() {

        //display data
        final int layoutstyle=R.layout.list_item_entries;
        int[] xml_id = new int[] {
                R.id.entry_dur,
                R.id.entry_title,
                R.id.entry_com,
                R.id.entry_start
        };
        String[] column = new String[] {
                "time_dur",
                "time_task",
                "time_com",
                "time_start"
        };
        Cursor row = db.fetchAllData(activity);
        adapter = new SimpleCursorAdapter(activity, layoutstyle,row,column, xml_id, 0);

        //display data by filter
        final String time_search = sharedPref.getString("filter_timeBY", "time_task");
        sharedPref.edit().putString("filter_timeBY", "time_task").apply();
        filter.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        sumTotalTime();
                    }
                }, 200);
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return db.fetchDataByFilter(constraint.toString(),time_search);
            }
        });

        lv.setAdapter(adapter);

        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                Helper.put_entryStart(activity, row2.getString(row2.getColumnIndexOrThrow("time_start")));
                Helper.put_entryEnd(activity, row2.getString(row2.getColumnIndexOrThrow("time_end")));
                Helper.put_entryTask(activity, row2.getString(row2.getColumnIndexOrThrow("time_task")));
                Helper.put_entryCom(activity, row2.getString(row2.getColumnIndexOrThrow("time_com")));
                Helper.put_entryDur(activity, row2.getString(row2.getColumnIndexOrThrow("time_dur")));
                Helper.put_entrySeqno(activity, row2.getString(row2.getColumnIndexOrThrow("_id")));
                Helper.switchToActivity(activity, EditActivity.class);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String _id = row.getString(row.getColumnIndexOrThrow("_id"));

                Snackbar snackbar = Snackbar
                        .make(lv, R.string.entry_delete, Snackbar.LENGTH_LONG)
                        .setAction(R.string.toast_yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                db.delete(Integer.parseInt(_id));
                                setNotesList();
                            }
                        });
                snackbar.show();

                return true;
            }
        });

        sumTotalTime();
    }


    private void sumTotalTime() {

        sharedPref.edit().putString("total_time", "0").apply();
        View view = null;

        String value;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, lv);
            TextView et = (TextView) view.findViewById(R.id.entry_dur);
            value=et.getText().toString();
            double dur_saved = Double.parseDouble(sharedPref.getString("total_time", "0"));
            double dur_new = dur_saved + Double.parseDouble(value);
            sharedPref.edit().putString("total_time", String.valueOf(dur_new)).apply();
        }

        total = activity.getString(R.string.entry_total) + " " +
                Helper.dur_long(activity, sharedPref.getString("total_time", ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (filter_layout.getVisibility() == View.GONE) {
            setNotesList();
        }
        if (sharedPref.getBoolean("finish_app", false)) {
            sharedPref.edit().putBoolean("finish_app", false).apply();
            finish();
        }
        if (sharedPref.getBoolean("recreate_app", false)) {
            sharedPref.edit().putBoolean("recreate_app", false).apply();
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                Helper.switchToActivity(activity, SettingsActivity.class);
                return true;

            case R.id.action_summary:
                getShareText();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getShareText() {

        sharedPref.edit().putString("share_text", "").apply();
        View view = null;

        for (int i = 0; i < adapter.getCount(); i++) {

            view = adapter.getView(i, view, lv);

            TextView tv_title = (TextView) view.findViewById(R.id.entry_title);
            TextView tv_com = (TextView) view.findViewById(R.id.entry_com);
            TextView tv_start = (TextView) view.findViewById(R.id.entry_start);
            TextView tv_dur = (TextView) view.findViewById(R.id.entry_dur);

            String text_title = getString(R.string.share_task) + " " + tv_title.getText().toString() + "\n";
            String text_com = getString(R.string.share_com) + " " + tv_com.getText().toString() + "\n";
            String text_start = getString(R.string.share_start) + " " + tv_start.getText().toString() + "\n";
            String text_dur = getString(R.string.share_dur) + " " + tv_dur.getText().toString() + " " + getString(R.string.entry_hours) + "\n";
            String row = text_title + text_com + text_start + text_dur + "\n";

            sharedPref.edit().putString("share_text", sharedPref.getString("share_text", "") + row).apply();
        }

        final String text = sharedPref.getString("share_text", "") + total;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.action_summary));
        builder.setMessage(text);
        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.toast_share, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final CharSequence[] options = {
                        getString(R.string.toast_share_text),
                        getString(R.string.toast_share_textFile)};

                AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                builder2.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                builder2.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getString(R.string.toast_share_text))) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_summary));
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.toast_share_use))));
                        }
                        if (options[item].equals(getString(R.string.toast_share_textFile))) {

                            Helper.grantPermissionsStorage(activity);

                            try {

                                File directory = new File(Environment.getExternalStorageDirectory() + "/Android/data/timetracker.backup/");
                                if (!directory.exists()) {
                                    //noinspection ResultOfMethodCallIgnored
                                    directory.mkdirs();
                                }

                                File filepath = new File(directory, getString(R.string.action_summary) + ".txt");  // file path to save
                                FileWriter writer = new FileWriter(filepath);
                                writer.append(text);
                                writer.flush();
                                writer.close();

                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/txt");
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_summary));
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);

                                if (filepath.exists()) {
                                    Uri bmpUri = Uri.fromFile(filepath);
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                }

                                startActivity(Intent.createChooser(sharingIntent, (getString(R.string.toast_share_use))));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                final AlertDialog dialog2 = builder2.create();
                // Display the custom alert dialog on interface
                dialog2.show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

