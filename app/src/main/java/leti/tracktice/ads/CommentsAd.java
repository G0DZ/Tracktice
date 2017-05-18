package leti.tracktice.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class CommentsAd {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "comments_DB_v01.db";
    private static final String dbTable = "comments";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement,  comments_task, UNIQUE(comments_task))");
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

    public CommentsAd(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    public void insert(String comments_task) {
        if(!isExist(comments_task)) {
            sqlDb.execSQL("INSERT INTO comments (comments_task) VALUES('" + comments_task + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String comments_task){
        String query = "SELECT comments_task FROM comments WHERE comments_task='"+comments_task+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id, String comments_task) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET comments_task='"+comments_task+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData() {
        String[] columns = new String[]{"_id", "comments_task"};
        return sqlDb.query(dbTable, columns, null, null, null, null, "comments_task");
    }

    /**
     * Getting all labels
     * returns list of labels
     * */
    public ArrayList<String> getRecords(){
        ArrayList<String> data=new ArrayList<>();
        Cursor cursor = sqlDb.query(dbTable, new String[]{"comments_task"},null, null, null, null, null);
        String fieldToAdd;
        while(cursor.moveToNext()){
            fieldToAdd=cursor.getString(0);
            data.add(fieldToAdd);
        }
        cursor.close();  // do not forget to close the cursor after operation done
        return data;
    }
}
