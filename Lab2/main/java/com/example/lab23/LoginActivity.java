package com.example.lab23;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
public class LoginActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация визуальных компонентов и обработчиков событий
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка события нажатия кнопки входа
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (isValidUser(username, password)) {
                    // Вход успешен
                    // Например, перенаправьте пользователя на главную активность
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    showToast("Login successful");
                } else {
                    showToast("Invalid username or password");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showToast("onStart(): Activity is visible, but not in focus.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToast("onResume(): Activity is in focus and user interaction is possible.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        showToast("onPause(): Activity is going into the background, but not stopped.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        showToast("onStop(): Activity is no longer visible.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showToast("onDestroy(): Activity is destroyed.");
    }

    private boolean isValidUser(String username, String password) {
        // Здесь можно добавить логику проверки имени пользователя и пароля
        return "vadim".equals(username) && "vadim".equals(password);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
