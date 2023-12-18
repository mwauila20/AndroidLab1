package com.example.labor2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UserDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDatabase";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_USER_RECORDS = "user_records";
    private static final String COLUMN_RECORD_ID = "record_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_RECORD = "record";

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_PASSWORD + " TEXT" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_USER_RECORDS_TABLE = "CREATE TABLE " + TABLE_USER_RECORDS +
                "(" +
                COLUMN_RECORD_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_RECORD + " TEXT" +
                ")";
        db.execSQL(CREATE_USER_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_RECORDS);
        onCreate(db);
    }

    public void addRecord(String username, String record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, getUserId(username));
        values.put(COLUMN_RECORD, record);
        db.insert(TABLE_USER_RECORDS, null, values);
        db.close();
    }

    public ArrayList<String> getUserRecords(String username) {
        ArrayList<String> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_RECORD +
                " FROM " + TABLE_USER_RECORDS +
                " WHERE " + COLUMN_USER_ID + " = " + getUserId(username);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                records.add(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    public void deleteRecord(String username, String record) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USER_ID + " = ? AND " + COLUMN_RECORD + " = ?";
        String[] whereArgs = {String.valueOf(getUserId(username)), record};
        db.delete(TABLE_USER_RECORDS, whereClause, whereArgs);
        db.close();
    }
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean userExists = cursor.moveToFirst(); // Проверка, что курсор не пустой

        cursor.close();
        db.close();

        if (!userExists) {
            // Если пользователя не существует, добавляем его в базу данных
            addUser(username, password);
        }

        return userExists; // Возвращаем результат проверки
    }




    private int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }

        cursor.close();
        db.close();
        return userId;
    }
}
