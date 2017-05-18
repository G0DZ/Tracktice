package leti.tracktice.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import leti.tracktice.R;
import leti.tracktice.ads.CommentsAd;
import leti.tracktice.ads.Helper;

public class CommentsActivity extends AppCompatActivity {
    //calling variables
    private CommentsAd db;

    private ListView lv = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.setDefaultValues(CommentsActivity.this, R.xml.settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);


        lv = (ListView) findViewById(R.id.list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                View dialogView = View.inflate(CommentsActivity.this, R.layout.dialog_edit_title, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);

                builder.setView(dialogView);
                builder.setTitle(R.string.editEntry_title);
                builder.setPositiveButton(R.string.toast_yes, null);
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                // Display the custom alert dialog on interface
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String inputTag = edit_title.getText().toString().trim();

                                try {
                                    if(db.isExist(inputTag)){
                                        Snackbar.make(edit_title, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    }else{
                                        db.insert(inputTag);
                                        Snackbar.make(lv, R.string.editEntry_save, Snackbar.LENGTH_SHORT).show();
                                        setCommentList();
                                        dialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    Log.w("time_tracker", "Error Package name not found ", e);
                                    Snackbar snackbar = Snackbar
                                            .make(edit_title, R.string.toast_error, Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                        });
                    }
                });

                dialog.show();
                Helper.showKeyboard(CommentsActivity.this,edit_title);
            }
        });

        //calling EntriesAd
        db = new CommentsAd(CommentsActivity.this);
        db.open();

        setCommentList();
    }

    private void setCommentList() {

        //display data
        final int layoutstyle=R.layout.list_item_lists;
        int[] xml_id = new int[] {
                R.id.entry_title,
        };
        String[] column = new String[] {
                "comments_task",
        };
        final Cursor row = db.fetchAllData();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(CommentsActivity.this, layoutstyle, row, column, xml_id, 0);

        lv.setAdapter(adapter);

        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                if (sharedPref.getString("new_Intent", "").equals("share_comment")) {
                    Cursor row = (Cursor) lv.getItemAtPosition(position);
                    final String comments_task = row.getString(row.getColumnIndexOrThrow("comments_task"));
                    sharedPref.edit().putString("handleTextCom", comments_task).apply();
                    sharedPref.edit().putString("new_Intent", "").apply();
                    finish();
                } else if (sharedPref.getString("new_Intent", "").equals("filter_comment")) {
                    Cursor row = (Cursor) lv.getItemAtPosition(position);
                    final String comments_task = row.getString(row.getColumnIndexOrThrow("comments_task"));
                    sharedPref.edit().putString("handle_filterComment", comments_task).apply();
                    sharedPref.edit().putString("new_Intent", "").apply();
                    finish();
                } else {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    final String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                    final String comments_task = row2.getString(row2.getColumnIndexOrThrow("comments_task"));

                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                    View dialogView = View.inflate(CommentsActivity.this, R.layout.dialog_edit_title, null);

                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                    edit_title.setText(comments_task);

                    builder.setView(dialogView);
                    builder.setTitle(R.string.editEntry_title);
                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            String inputTag = edit_title.getText().toString().trim();
                            db.update(Integer.parseInt(_id), inputTag);
                            setCommentList();
                            Snackbar.make(lv, R.string.editEntry_save, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog dialog2 = builder.create();
                    // Display the custom alert dialog on interface
                    dialog2.show();
                    Helper.showKeyboard(CommentsActivity.this,edit_title);
                }
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
                                setCommentList();
                            }
                        });
                snackbar.show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setCommentList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
