package leti.tracktice.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import leti.tracktice.R;

public class EntriesAd {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "time_DB_v01.db";
    private static final String dbTable = "time";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, time_task, time_com, time_dur, time_start, time_end, UNIQUE(time_start))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
            onCreate(db);
        }
    }

    //establish connection with SQLiteDataBase
    private final Context c;
    private SQLiteDatabase sqlDb;

    public EntriesAd(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    public void insert(String time_task,String time_com,String time_dur,String time_start, String time_end) {
        if(!isExist(time_task)) {
            sqlDb.execSQL("INSERT INTO time (time_task, time_com, time_dur, time_start, time_end) VALUES('" + time_task + "','" + time_com + "','" + time_dur + "','" + time_start + "','" + time_end + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String time_start){
        String query = "SELECT time_start FROM time WHERE time_start='"+time_start+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String time_task,String time_com,String time_dur,String time_start, String time_end) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET time_task='"+time_task+"', time_com='"+time_com+"', time_dur='"+time_dur+"', time_start='"+time_start+"', time_end='"+time_end+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData(Context context) {

        PreferenceManager.setDefaultValues(context, R.xml.settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String[] columns = new String[]{"_id", "time_task", "time_com", "time_dur","time_start","time_end"};

        if (sp.getString("sortDB", "time_task").equals("time_task")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "time_task");

        } else if (sp.getString("sortDB", "time_task").equals("time_dur")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "time_dur");

        } else if (sp.getString("sortDB", "time_task").equals("time_start")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "time_start");

        } else if (sp.getString("sortDB", "time_task").equals("time_com")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "time_com");
        }

        return null;
    }

    //fetch data by filter
    public Cursor fetchDataByFilter(String inputText,String filterColumn) throws SQLException {
        Cursor row;
        String query = "SELECT * FROM "+dbTable;
        if (inputText == null  ||  inputText.length () == 0)  {
            row = sqlDb.rawQuery(query, null);
        }else {
            query = "SELECT * FROM "+dbTable+" WHERE "+filterColumn+" like '%"+inputText+"%'";
            row = sqlDb.rawQuery(query, null);
        }
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }
}

