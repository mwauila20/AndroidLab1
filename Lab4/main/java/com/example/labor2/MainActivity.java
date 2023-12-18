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
    private static final String KEY_RECORDS_PREFIX = "records_";

    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText editText;
    private Button addButton, deleteButton, languageButton;
    private Locale currentLocale;
    private TextView usernameTextView;

    private String username;

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

        // Загрузка сохраненного языка
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        //String savedLanguage = preferences.getString(KEY_LANGUAGE, null);
//if(savedLanguage.equals("ru")) {
   // setLocale("ru");
//}
//else {
   // setLocale("en");
//}


        // Загрузка сохраненных записей для конкретного пользователя
        String savedRecordsJson = preferences.getString(KEY_RECORDS_PREFIX + username, null);
        if (savedRecordsJson != null) {
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            dataList = new Gson().fromJson(savedRecordsJson, listType);
        } else {
            dataList = new ArrayList<>();
        }

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView, dataList);
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
                    dataList.add(newItem);
                    adapter.notifyDataSetChanged();
                    editText.setText("");

                    // Сохранение записей для конкретного пользователя сразу после добавления
                    saveRecords();
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
                saveRecords();
            }
        });
    }

    private void deleteSelectedRecords() {
        for (int i = dataList.size() - 1; i >= 0; i--) {
            View item = listView.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                dataList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();

        // Сохранение записей после удаления
        saveRecords();
    }
    private void setLocale(String lang) {
        Locale newLocale = new Locale(lang);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.locale = newLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // Обновите текст кнопок на основе текущей локали
        addButton.setText(R.string.add_record);
        deleteButton.setText(R.string.delete_record);
        if(lang=="en") {
            editText.setHint("Add new record");
        }
        else {
            editText.setHint("Добавьте новую запись");
        }
        // Обновите текущую локаль
        currentLocale = newLocale;
    }
    // Остальной код остается неизменным

    private void saveRecords() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANGUAGE, currentLocale.getLanguage());

        // Сохранение записей для конкретного пользователя
        String recordsJson = new Gson().toJson(dataList);
        editor.putString(KEY_RECORDS_PREFIX + username, recordsJson);

        editor.apply();
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedLanguage = preferences.getString(KEY_LANGUAGE, null);
        if(savedLanguage.equals("ru")) {
            setLocale("ru");
        }
        else {
            setLocale("en");
        }
    }
}
