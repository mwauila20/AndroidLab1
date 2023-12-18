package com.example.lab23;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Locale;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText editText;
    private Button addButton, deleteButton, languageButton;
    private Locale currentLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = new ArrayList<>();
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
                int b=0;
                String newItem = editText.getText().toString();
                for (int i = dataList.size() - 1; i >= 0; i--) {
                    if (newItem.equals(dataList.get(i)) ) {
                        b++;

                    }
                }
                if(b==0) {
                    dataList.add(newItem);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You've tried to add an existing record",Toast.LENGTH_LONG).show();
                }


                }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = dataList.size() - 1; i >= 0; i--) {
                    View item = listView.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    if (checkBox.isChecked()) {
                        dataList.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
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
            }
        });
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

        // Обновите текущую локаль
        currentLocale = newLocale;
    }
}
