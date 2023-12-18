package com.example.labor2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {

    private EditText newPasswordEditText;
    private Button confirmButton;
    private UserDatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmButton = findViewById(R.id.confirmButton);

        dbHandler = new UserDatabaseHandler(this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = getIntent().getStringExtra("username");
                final String newPassword = newPasswordEditText.getText().toString();

                if (!newPassword.isEmpty()) {
                    // Use a separate thread to perform the database operation
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbHandler.updateUserPassword(username, newPassword);
                            showToast("Password updated successfully");
                            finish(); // Close the activity after a successful password update
                        }
                    }).start();
                } else {
                    showToast("Please enter a new password");
                }
            }
        });
    }

    private void showToast(final String message) {
        // Ensure UI updates on the main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
