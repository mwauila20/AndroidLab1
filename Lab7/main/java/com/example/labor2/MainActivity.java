package com.example.labor2;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

        // Get the username from the Intent
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
                final String newItem = editText.getText().toString();

                // Use AsyncTask to perform the database operation
                new DatabaseAsyncTask(new Runnable() {
                    @Override
                    public void run() {
                        if (!newItem.isEmpty()) {
                            dbHandler.addRecord(username, newItem);

                            // Update the UI on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadRecords();
                                    editText.setText("");
                                }
                            });
                        }
                    }
                }).execute();
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
                saveLanguage(); // Save the language in the database
                loadRecords(); // Load records from the database
            }
        });
    }

    private void deleteSelectedRecords() {
        // Use AsyncTask to perform the database delete operation
        new DatabaseAsyncTask(new Runnable() {
            @Override
            public void run() {
                // Delete selected records from the database
                for (int i = 0; i < listView.getCount(); i++) {
                    View item = listView.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    if (checkBox.isChecked()) {
                        String recordToDelete = adapter.getItem(i);
                        dbHandler.deleteRecord(username, recordToDelete);
                    }
                }

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadRecords(); // Load records from the database
                    }
                });
            }
        }).execute();
    }

    private void setLocale(String lang) {
        Locale newLocale = new Locale(lang);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.locale = newLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        currentLocale = newLocale;
    }

    private void saveRecords() {
        // Save records to the database
        ArrayList<String> records = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            records.add(adapter.getItem(i));
        }
        dbHandler.saveUserRecords(username, records);
    }

    private void loadRecords() {
        // Load records from the database
        ArrayList<String> records = dbHandler.getUserRecords(username);
        adapter.clear();
        adapter.addAll(records);
        adapter.notifyDataSetChanged();
    }

    private void saveLanguage() {
        // Save the language to the database
        dbHandler.saveUserLanguage(username, currentLocale.getLanguage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Load records from the database when the activity starts
        loadRecords();
    }

}
