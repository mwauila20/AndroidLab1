package com.example.labor2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String KEY_LANGUAGE = "language";

    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText editText;
    private Button addButton, deleteButton, languageButton;
    private Locale currentLocale;
    private TextView usernameTextView;

    private String username;
    private UserDatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameTextView = findViewById(R.id.usernameTextView);

        // Получение имени пользователя из Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            usernameTextView.setText("Welcome, " + username);
        }

        dbHandler = new UserDatabaseHandler(this);

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView, new ArrayList<>());
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        editText = findViewById(R.id.editText);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        languageButton = findViewById(R.id.languageButton);

        currentLocale = getResources().getConfiguration().locale;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = editText.getText().toString();
                if (!newItem.isEmpty()) {
                    dbHandler.addRecord(username, newItem); // Сохранение записи в базе данных
                    loadRecords(); // Загрузка записей из базы данных
                    editText.setText("");
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedRecords();
            }
        });

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocale.getLanguage().equals("ru")) {
                    setLocale("en");
                } else {
                    setLocale("ru");
                }
                saveRecords(); // Сохранение записей в базе данных
            }
        });
    }

    private void deleteSelectedRecords() {
        // Удаление выбранных записей из базы данных
        for (int i = 0; i < listView.getCount(); i++) {
            View item = listView.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                String recordToDelete = adapter.getItem(i);
                dbHandler.deleteRecord(username, recordToDelete);
            }
        }
        loadRecords(); // Загрузка записей из базы данных
    }

    private void setLocale(String lang) {
        Locale newLocale = new Locale(lang);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.locale = newLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // ... остальной код без изменений
        currentLocale = newLocale;
    }

    private void saveRecords() {
        // Сохранение текущего языка
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANGUAGE, currentLocale.getLanguage());
        editor.apply();
    }

    private void loadRecords() {
        // Загрузка записей из базы данных
        ArrayList<String> records = dbHandler.getUserRecords(username);
        adapter.clear();
        adapter.addAll(records);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Загрузка записей из базы данных при старте активности
        loadRecords();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.close();
    }
}
