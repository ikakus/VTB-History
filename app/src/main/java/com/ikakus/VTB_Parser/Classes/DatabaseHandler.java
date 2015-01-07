package com.ikakus.VTB_Parser.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "VTBSMSReader.db";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String DATE_TYPE = "DATE";

    // Contacts Table Columns names
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SmsEntry.TABLE_NAME + "(" +
                    SmsEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    SmsEntry.COLUMN_NAME_FROM + TEXT_TYPE + COMMA_SEP +
                    SmsEntry.COLUMN_NAME_SMSBODY + TEXT_TYPE + COMMA_SEP +
                    SmsEntry.COLUMN_NAME_DATE +  TEXT_TYPE + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null ? true : false;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SmsEntry.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void addSms(SMSMessage smsMessage) {
        SQLiteDatabase database = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        ContentValues values = new ContentValues();
        values.put(SmsEntry.COLUMN_NAME_FROM, smsMessage.getSender());
        values.put(SmsEntry.COLUMN_NAME_SMSBODY, smsMessage.getBody());
        values.put(SmsEntry.COLUMN_NAME_DATE, dateFormat.format(smsMessage.getDate()));

        database.insert(SmsEntry.TABLE_NAME, null, values);
        database.close();
    }

    // Getting single SMSMessage


    // Getting All Contacts
    public List<String> getAllSmsString() {
        List<String> stringList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SmsEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String result = cursor.getString(0) + cursor.getString(1) + cursor.getString(2);
                // Adding contact to list
                stringList.add(result);
            } while (cursor.moveToNext());
        }

        // return contact list
        return stringList;
    }

    public List<SMSMessage> getAllSms() {
        List<SMSMessage> smsMessagesList = new ArrayList<SMSMessage>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SmsEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String from = cursor.getString(1);
                String body = cursor.getString(2);
                String date = cursor.getString(3);

                SMSMessage  smsMessage = new SMSMessage(from,body,new Date(date));
                smsMessagesList.add(smsMessage);
            } while (cursor.moveToNext());
        }

        // return contact list
        return smsMessagesList;
    }

    // Getting contacts Count
    public int getSmsCount() {
        String countQuery = "SELECT  * FROM " + SmsEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public static abstract class SmsEntry implements BaseColumns {
        public static final String TABLE_NAME = "vtbsms";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_FROM = "title";
        public static final String COLUMN_NAME_SMSBODY = "smsbody";

        public static final String COLUMN_NAME_DATE = "date";
    }
}
