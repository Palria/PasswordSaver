package com.palria.passwordsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.widget.Toast;

public class NotesSQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static  String DATABASE_NAME = "passwordsaver.notesdatabase";

    public static final String TABLE_NAME = "LOCAL_NOTES";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_BODY = "BODY";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_DATE_UPDATED = "COLUMN_DATE_UPDATED";
    public static final String COLUMN_IS_UPLOADED_ONLINE = "IS_UPLOADED_ONLINE";

    Context context;

    public NotesSQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
//        DATABASE_NAME = GlobalConfig.getCurrentUserId();
initDatabase(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_BODY + " TEXT ,"+
                COLUMN_DATE + " TEXT ,"+
                COLUMN_IS_UPLOADED_ONLINE + " TEXT ,"+
                 COLUMN_DATE_UPDATED + " TEXT )";

        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        db.execSQL(SQL_DELETE);
        onCreate(db);


    }

    void initDatabase(Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, "INIT_ID");
//        db.insert(TABLE_NAME, null, values);


    }
    public void insertData(String id, String name, String body, String dateAdded,String isUploadedOnline, GlobalConfig.ActionCallback actionCallback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_DATE, dateAdded);
        values.put(COLUMN_IS_UPLOADED_ONLINE, isUploadedOnline);
        values.put(COLUMN_DATE_UPDATED, GlobalConfig.getDate());


        // Insert new row
        // Insert SQL Statement
        try{
        long result = db.insert(TABLE_NAME, null, values);

        // To check data is inserted or not
        if (result == -1) {
            Toast toast = Toast.makeText(context, "Data not saved ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            actionCallback.onFailed("Data not saved");
        } else {
            Toast toast = Toast.makeText(context, "Data Saved Successfully!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            actionCallback.onSuccess();

        }
    }catch(Exception e){}

}

    public void updateData(String id, String name, String body,String isUploadedOnline, GlobalConfig.ActionCallback actionCallback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_IS_UPLOADED_ONLINE, isUploadedOnline);
        values.put(COLUMN_DATE_UPDATED, GlobalConfig.getDate());
        // Which row to update , based on the ID
        // Update SQL Statement
        try {
            long result = db.update(TABLE_NAME, values, "ID = ?", new String[]{id});


        // To check data updated or not
        if (result == -1) {
            Toast.makeText(context, "Data not Updated", Toast.LENGTH_SHORT).show();
//            actionCallback.onFailed("Data not updated");

        } else {
            Toast toast = Toast.makeText(context, "Data Updated Successfully!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            actionCallback.onSuccess();

        }
        }catch(Exception e){}

    }

    void deleteData(String id, GlobalConfig.ActionCallback actionCallback) {
        SQLiteDatabase db = this.getWritableDatabase();

        // SQL Statement for Delete
        long result = db.delete(TABLE_NAME, "ID = ?", new String[]{id});

        if (result == -1) {
            Toast.makeText(context, "Data not Deleted", Toast.LENGTH_SHORT).show();
//            actionCallback.onFailed("Data not deleted");

        } else {
//            Toast.makeText(context, "Data Successfully Deleted", Toast.LENGTH_SHORT).show();
            actionCallback.onSuccess();

        }
    }

    public Cursor getNotes() {
        SQLiteDatabase db = this.getWritableDatabase();

        // SQL Statement
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = null;
        try{
            cursor = db.rawQuery(query, null);
        }catch(Exception e){}


        return cursor;


    }

    public boolean exists(String columnId){
        SQLiteDatabase db = this.getWritableDatabase();

        // Which row to search , based on the ID
        String[] columns = new String[]{COLUMN_ID};
        Cursor cursor = null;
        if(context.getDatabasePath(DATABASE_NAME).exists()) {
            cursor = db.query(TABLE_NAME, columns, "ID = ?", new String[]{columnId},
                    null, null, null, null);

            return cursor.getCount() > 0;
        }
        if(cursor!=null)cursor.close();
        return false;
    }

    public Cursor searchData(String id) {

        SQLiteDatabase db = this.getWritableDatabase();


        // Which row to search , based on the ID
        String[] columns = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_BODY,COLUMN_DATE,COLUMN_IS_UPLOADED_ONLINE};
        Cursor cursor = db.query(TABLE_NAME, columns, "ID = ?", new String[]{id},
                null, null, null, null);

        return cursor;

    }


    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

}
