package com.example.labor2;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, changePasswordButton, deleteUserButton;
    private UserDatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);

        dbHandler = new UserDatabaseHandler(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                // Use AsyncTask to perform the database operation
                new DatabaseAsyncTask(new Runnable() {
                    @Override
                    public void run() {
                        if (dbHandler.checkUser(username, password)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            showToast("Login successful");
                        } else {
                            showToast("Invalid username or password");
                        }
                    }
                }).execute();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", usernameEditText.getText().toString());
                startActivity(intent);
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();

                // Use AsyncTask to perform the database operation
                new DatabaseAsyncTask(new Runnable() {
                    @Override
                    public void run() {
                        if (!username.isEmpty()) {
                            // Delete user from the database
                            dbHandler.deleteUser(username);
                            showToast("User deleted successfully");

                            // Clear fields after deleting the user
                            usernameEditText.setText("");
                            //passwordEditText.setText("");
                        } else {
                            showToast("Please enter a username to delete");
                        }
                    }
                }).execute();
            }
        });
    }

    private void showToast(final String message) {
        // Ensure UI updates on the main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
