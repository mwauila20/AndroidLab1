package com.example.labor2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class UserDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDatabase";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_LANGUAGE = "language";

    private static final String TABLE_USER_RECORDS = "user_records";
    private static final String COLUMN_RECORD_ID = "record_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_RECORD = "record";

    private Context context; // Добавлено поле context

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_LANGUAGE + " TEXT" +
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

    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        //values.put(COLUMN_LANGUAGE, "en"); // Значение по умолчанию для языка
        long userId = db.insert(TABLE_USERS, null, values);
       // db.close();
        return userId;
    }

    public int addRecord(long userId, String record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RECORD, record);
        int userRecordId = (int) db.insert(TABLE_USER_RECORDS, null, values);
       // db.close();
        return userRecordId;
    }

    public void addRecord(String username, String record) {
        SQLiteDatabase db = this.getWritableDatabase();
        long userId = getUserId(username);

        if (userId != -1) {
            int userRecordId = addRecord(userId, record);
            if (userRecordId == -1) {
                showToast("Error adding record for the user");
            }
        }

       // db.close();
    }

    public ArrayList<String> getUserRecords(String username) {
        ArrayList<String> records = new ArrayList<>();
        long userId = getUserId(username);

        if (userId != -1) {
            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT " + COLUMN_RECORD +
                    " FROM " + TABLE_USER_RECORDS +
                    " WHERE " + COLUMN_USER_ID + " = " + userId;

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_RECORD);

                if (columnIndex != -1) {
                    do {
                        records.add(cursor.getString(columnIndex));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
          //  db.close();
        }

        return records;
    }

    public void deleteRecord(String username, String record) {
        SQLiteDatabase db = this.getWritableDatabase();
        long userId = getUserId(username);

        if (userId != -1) {
            String whereClause = COLUMN_USER_ID + " = ? AND " + COLUMN_RECORD + " = ?";
            String[] whereArgs = {String.valueOf(userId), record};
            db.delete(TABLE_USER_RECORDS, whereClause, whereArgs);
        }

       // db.close();
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean userExists = cursor.moveToFirst();

        cursor.close();
       // db.close();

        if (!userExists) {
            long userId = addUser(username, password);
            if (userId != -1) {
                int userRecordId = addRecord(userId, "Default Record");
                if (userRecordId == -1) {
                    showToast("Error adding default record for the new user");
                }
            } else {
                showToast("Error adding a new user");
            }
        }

        return userExists;
    }

    private int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        int userId = -1;
        int columnIndex = cursor.getColumnIndex(COLUMN_ID);

        if (columnIndex != -1 && cursor.moveToFirst()) {
            userId = cursor.getInt(columnIndex);
        }

        cursor.close();
       // db.close();
        return userId;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void saveUserRecords(String username, ArrayList<String> records) {
        SQLiteDatabase db = this.getWritableDatabase();
        long userId = getUserId(username);

        if (userId != -1) {
            // Удаляем существующие записи пользователя
            String whereClause = COLUMN_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(userId)};
            db.delete(TABLE_USER_RECORDS, whereClause, whereArgs);

            // Добавляем новые записи пользователя
            for (String record : records) {
                addRecord(userId, record);
            }
        }

        //db.close();
    }
    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Определяем условие для удаления пользователя
        String whereClauseUsers = COLUMN_USERNAME + " = ?";
        String[] whereArgsUsers = {username};
        long userId = getUserId(username);
        if (userId != -1) {
            String whereClauseRecords = COLUMN_USER_ID + " = ?";
            String[] whereArgsRecords = {String.valueOf(userId)};
            db.delete(TABLE_USER_RECORDS, whereClauseRecords, whereArgsRecords);
        }
        // Удаляем пользователя
        db.delete(TABLE_USERS, whereClauseUsers, whereArgsUsers);

        // Удаляем все записи пользователя


        // Закрываем базу данных
        db.close();
    }

    public void updateUserPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Подготавливаем новое значение для обновления
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Определяем условие для обновления записи
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        // Выполняем обновление записи
        int count = db.update(
                TABLE_USERS,
                values,
                selection,
                selectionArgs);

        // Печатаем в лог количество обновленных строк (можно удалить в продакшене)
        Log.d("UserDatabaseHandler", "Updated records: " + count);

        // Закрываем базу данных
        // db.close(); // Это вы можете закомментировать, так как база данных уже закрывается в onDestroy активити
    }
    public void saveUserLanguage(String username, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        long userId = getUserId(username);

        if (userId != -1) {
            // Обновляем язык пользователя
            ContentValues values = new ContentValues();
            values.put(COLUMN_LANGUAGE, language);
            String whereClause = COLUMN_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(userId)};
           // db.update(TABLE_USERS, values, whereClause, whereArgs);
        }

        //db.close();
    }
}
