package com.example.myfirstapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class HelloActivity extends Activity {
    private Button button1;
    private Button button2;
    private TextView textViewCount1;
    private TextView textViewCount2;
    private int clickCount1 = 0;
    private int clickCount2 = 0;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloact);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        textViewCount1 = findViewById(R.id.textViewCount1);
        textViewCount2 = findViewById(R.id.textViewCount2);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setText("НАЖАТО!");
                clickCount1++;
                textViewCount1.setText("Количество нажатий кнопки 1: " + clickCount1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setText("НАЖАТО!");
                clickCount2++;
                textViewCount2.setText("Количество нажатий кнопки 2: " + clickCount2);
            }
        });
    }
}

