package com.royal.educare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword;
    private MaterialButton btnSignup;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // Set Click Listeners
        btnSignup.setOnClickListener(v -> handleSignup());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleSignup() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || 
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignup.setEnabled(false);

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            com.royal.educare.data.AppDatabase db = com.royal.educare.data.AppDatabase.getDatabase(getApplicationContext());
            com.royal.educare.data.User existingUser = db.userDao().getUserByEmail(email);

            if (existingUser != null) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    btnSignup.setEnabled(true);
                });
                return;
            }

            com.royal.educare.data.User newUser = new com.royal.educare.data.User(firstName, lastName, email, password);
            db.userDao().insertUser(newUser);

            runOnUiThread(() -> {
                Toast.makeText(SignupActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
