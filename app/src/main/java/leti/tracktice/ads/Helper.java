package leti.tracktice.ads;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import leti.tracktice.R;

public class Helper {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static void grantPermissionsStorage(final Activity from) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = from.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!from.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    new AlertDialog.Builder(from)
                            .setTitle(R.string.app_permissions_title)
                            .setMessage(Helper.textSpannable(from.getString(R.string.app_permissions)))
                            .setPositiveButton(from.getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (android.os.Build.VERSION.SDK_INT >= 23)
                                        from.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            })
                            .setNegativeButton(from.getString(R.string.toast_cancel), null)
                            .show();
                    return;
                }
                from.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    public static SpannableString textSpannable(String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    public static void switchToActivity(Activity from, Class to) {
        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        from.startActivity(intent);
    }

    public static void showKeyboard(final Activity from, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) from.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.setSelection(editText.length());
            }
        }, 200);
    }

    public static String dur_long (Activity activity, String duration) {
        double w;

        try {
            w = Double.valueOf(duration);
        } catch (NumberFormatException e) {
            w = 0; // your default value
        }

        double diff = w * 1000*3600;
        long diff_rounded = Math.round(diff);
        long diffMinutes = diff_rounded / (60 * 1000) % 60;
        long diffHours = diff_rounded / (60 * 60 * 1000) % 24;
        long diffDays = diff_rounded / (24 * 60 * 60 * 1000);
        String days = Long.toString(diffDays);
        String hours = Long.toString(diffHours);
        String minutes = Long.toString(diffMinutes);

        return days + " " + activity.getString(R.string.entry_days) + " " +
                hours + " " + activity.getString(R.string.entry_hours) + " " +
                minutes + " " + activity.getString(R.string.entry_minutes);
    }

    // Edit entry

    public static String get_entryStart (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextStart", "");
    }
    public static String get_entryEnd (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextEnd", "");
    }
    public static String get_entryTask (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextTask", "");
    }
    public static String get_entryCom (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextCom", "");
    }
    public static String get_entrySeqno (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextSeqno", "");
    }
    public static String get_entryDur (Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("handleTextDur", "");
    }

    public static void put_entryStart (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextStart", value).apply();
    }
    public static void put_entryEnd (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextEnd", value).apply();
    }
    public static void put_entryTask (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextTask", value).apply();
    }
    public static void put_entryCom (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextCom", value).apply();
    }
    public static void put_entrySeqno (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextSeqno", value).apply();
    }
    public static void put_entryDur (Activity activity, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString("handleTextDur", value).apply();
    }


}

