package leti.tracktice.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import leti.tracktice.R;
import leti.tracktice.ads.Helper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setTitle(R.string.action_settings);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the activity_screen_main content
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {


        private void addEditTasksListener() {

            final Activity activity = getActivity();
            Preference reset = findPreference("edit_tasks");

            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    Helper.switchToActivity(activity, TasksActivity.class);
                    return true;
                }
            });
        }

        private void addEditCommentsListener() {

            final Activity activity = getActivity();
            Preference reset = findPreference("edit_comments");

            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    Helper.switchToActivity(activity, CommentsActivity.class);
                    return true;
                }
            });
        }

        private void addDelete_dbListener() {

            Preference reset = findPreference("delete");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
                    final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    final CharSequence[] options = {
                            getString(R.string.pref_backup_entries),
                            getString(R.string.pref_backup_tasks),
                            getString(R.string.pref_backup_comments),
                            getString(R.string.pref_backup_all)};
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.pref_delete))
                            .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options[item].equals(getString(R.string.pref_backup_entries))) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.toast_confirmation_title))
                                                .setMessage(getString(R.string.toast_confirmation))
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        sharedPref.edit().putBoolean("recreate_app", true).apply();
                                                        getActivity().deleteDatabase("time_DB_v01.db");
                                                        Toast.makeText(getActivity(), R.string.toast_delete, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                    if (options[item].equals(getString(R.string.pref_backup_tasks))) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.toast_confirmation_title))
                                                .setMessage(getString(R.string.toast_confirmation))
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        getActivity().deleteDatabase("tasks_DB_v01.db");
                                                        Toast.makeText(getActivity(), R.string.toast_delete, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                    if (options[item].equals(getString(R.string.pref_backup_comments))) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.toast_confirmation_title))
                                                .setMessage(getString(R.string.toast_confirmation))
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        getActivity().deleteDatabase("comments_DB_v01.db");
                                                        Toast.makeText(getActivity(), R.string.toast_delete, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                    if (options[item].equals(getString(R.string.pref_backup_all))) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.toast_confirmation_title))
                                                .setMessage(getString(R.string.toast_confirmation))
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        sharedPref.edit().putBoolean("recreate_app", true).apply();
                                                        getActivity().deleteDatabase("time_DB_v01.db");
                                                        getActivity().deleteDatabase("tasks_DB_v01.db");
                                                        getActivity().deleteDatabase("comments_DB_v01.db");
                                                        Toast.makeText(getActivity(), R.string.toast_delete, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                }
                            }).show();

                    return true;
                }
            });
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
            addEditTasksListener();
            addEditCommentsListener();
            addDelete_dbListener();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}