package com.example.labor2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends Activity {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANGUAGE = "language";
    private static final String PREFS_NAME = "MyPrefsFile";

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    // Список зарегистрированных пользователей и их паролей
    private Map<String, String> userPasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация визуальных компонентов и обработчиков событий
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Загрузка списка зарегистрированных пользователей и их паролей из SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userPasswords = loadUserPasswords(preferences);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка события нажатия кнопки входа
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (isUserRegistered(username)) {
                    // Если пользователь зарегистрирован, проводим проверку пароля
                    if (isValidPassword(username, password)) {
                        // Вход успешен
                        // Например, перенаправьте пользователя на главную активность
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        showToast("Login successful");
                    } else {
                        showToast("Invalid password");
                    }
                } else {
                    // Если пользователя нет в списке, то добавляем его, сохраняем пароль и сохраняем в SharedPreferences
                    registerUser(username, password);
                    saveUserPasswords();
                    showToast("User registered");
                }
            }
        });
    }

    // Метод для проверки, зарегистрирован ли пользователь
    private boolean isUserRegistered(String username) {
        return userPasswords.containsKey(username);
    }

    // Метод для проверки правильности пароля пользователя
    private boolean isValidPassword(String username, String password) {
        String storedPassword = userPasswords.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    // Метод для регистрации нового пользователя и сохранения его пароля
    private void registerUser(String username, String password) {
        userPasswords.put(username, password);
    }

    // Сохранение списка пользователей и их паролей в SharedPreferences
    private void saveUserPasswords() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Преобразование Map в Set<String>
        Set<String> userPasswordsSet = new HashSet<>();
        for (Map.Entry<String, String> entry : userPasswords.entrySet()) {
            userPasswordsSet.add(entry.getKey() + ":" + entry.getValue());
        }

        editor.putStringSet("userPasswords", userPasswordsSet);
        editor.apply();
    }

    // Загрузка списка пользователей и их паролей из SharedPreferences
    private Map<String, String> loadUserPasswords(SharedPreferences preferences) {
        Map<String, String> userPasswords = new HashMap<>();
        Set<String> userPasswordsSet = preferences.getStringSet("userPasswords", new HashSet<String>());

        // Преобразование Set<String> в Map
        for (String entry : userPasswordsSet) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                userPasswords.put(parts[0], parts[1]);
            }
        }

        return userPasswords;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
