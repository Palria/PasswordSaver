package com.palria.passwordsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PasswordsSQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "passwordsaver.passworddatabase";

    public static final String TABLE_NAME = "LOCAL_PASSWORDS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_BODY = "BODY";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_DATE_UPDATED = "COLUMN_DATE_UPDATED";
    public static final String COLUMN_IS_UPLOADED_ONLINE = "IS_UPLOADED_ONLINE";
    public static final String PASSWORD = "PASSWORD";
    public static final String PIN = "PIN";
    public static final String TOKEN = "TOKEN";
    public static final String CODE = "CODE";
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String ITEM_TITLES = "ITEM_TITLES";
    public static final String ITEM_VALUES = "ITEM_VALUES";


    Context context;

    public PasswordsSQLiteDatabaseHelper(Context context) {
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
                COLUMN_DATE_UPDATED + " TEXT ,"+
                PASSWORD + " TEXT ,"+
                PIN + " TEXT ,"+
                TOKEN + " TEXT ,"+
                CODE + " TEXT ,"+
                PHONE + " TEXT ,"+
                EMAIL + " TEXT ,"+
                ITEM_TITLES + " TEXT ,"+
                ITEM_VALUES + " TEXT )";

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
    public void insertData(String id, String name, String body, String dateAdded,String isUploadedOnline
            ,String password,String pin,String token,String code,String phone,String email,String itemTitles,String itemValues, GlobalConfig.ActionCallback actionCallback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_DATE, dateAdded);
        values.put(COLUMN_IS_UPLOADED_ONLINE, isUploadedOnline);
        values.put(COLUMN_DATE_UPDATED, GlobalConfig.getDate());
        values.put(PASSWORD, password);
        values.put(PIN, pin);
        values.put(TOKEN, token);
        values.put(CODE, code);
        values.put(PHONE, phone);
        values.put(EMAIL, email);
        values.put(ITEM_TITLES, itemTitles);
        values.put(ITEM_VALUES, itemValues);


        // Insert new row
        // Insert SQL Statement
        try{
        long result = db.insert(TABLE_NAME, null, values);

        // To check data is inserted or not
        if (result == -1) {
            Toast.makeText(context, "Data not saved ", Toast.LENGTH_SHORT).show();
            actionCallback.onFailed("Data not saved");
        } else {
            Toast.makeText(context, "Data saved Successfully", Toast.LENGTH_SHORT).show();
            actionCallback.onSuccess();

        }
    }catch(Exception e){}

}

    public void updateData(String id, String name, String body,String isUploadedOnline
            ,String password,String pin,String token,String code,String phone,String email,String itemTitles,String itemValues, GlobalConfig.ActionCallback actionCallback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_IS_UPLOADED_ONLINE, isUploadedOnline);
        values.put(COLUMN_DATE_UPDATED, GlobalConfig.getDate());

        values.put(PASSWORD, password);
        values.put(PIN, pin);
        values.put(TOKEN, token);
        values.put(CODE, code);
        values.put(PHONE, phone);
        values.put(EMAIL, email);
        values.put(ITEM_TITLES, itemTitles);
        values.put(ITEM_VALUES, itemValues);
        // Which row to update , based on the ID
        // Update SQL Statement
        try {
            long result = db.update(TABLE_NAME, values, "ID = ?", new String[]{id});


        // To check data updated or not
        if (result == -1) {
            Toast.makeText(context, "Data not Updated", Toast.LENGTH_SHORT).show();
//            actionCallback.onFailed("Data not updated");

        } else {
//            Toast.makeText(context, "Data Updated Successfully!", Toast.LENGTH_SHORT).show();
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

    public Cursor getData() {
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
        if(context.getDatabasePath(DATABASE_NAME).exists()) {
            Cursor cursor = db.query(TABLE_NAME, columns, "ID = ?", new String[]{columnId},
                    null, null, null, null);

            return cursor.getCount() > 0;
        }
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
