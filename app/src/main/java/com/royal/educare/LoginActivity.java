package com.royal.educare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // Set Click Listeners
        btnLogin.setOnClickListener(v -> handleLogin());
        tvSignup.setOnClickListener(v -> navigateToSignup());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            com.royal.educare.data.AppDatabase db = com.royal.educare.data.AppDatabase.getDatabase(getApplicationContext());
            com.royal.educare.data.User loginUser = db.userDao().getUserByEmailAndPassword(email, password);

            if (loginUser != null) {
                // Save session using SharedPreferences
                android.content.SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
                prefs.edit().putInt("userId", loginUser.id).apply();

                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                });
            }
        });
    }

    private void navigateToSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}
