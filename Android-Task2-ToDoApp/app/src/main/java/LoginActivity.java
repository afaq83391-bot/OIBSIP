package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.models.User;
import com.example.todoapp.utils.PasswordHasher;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            dbHelper = new DatabaseHelper(this);
            sessionManager = new SessionManager(this);

            if (sessionManager.isLoggedIn()) {
                navigateToMain();
                return;
            }

            tilEmail = findViewById(R.id.til_login_email);
            tilPassword = findViewById(R.id.til_login_password);
            etEmail = findViewById(R.id.et_login_email);
            etPassword = findViewById(R.id.et_login_password);
            btnLogin = findViewById(R.id.btn_login);
            tvGoToRegister = findViewById(R.id.tv_goto_register);

            btnLogin.setOnClickListener(v -> attemptLogin());
            tvGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(0, 0);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Login Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        boolean valid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            valid = false;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            valid = false;
        }
        if (!valid) return;

        String hashedPassword = PasswordHasher.hash(password);
        User user = dbHelper.authenticateUser(email, hashedPassword);

        if (user != null) {
            sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail());
            Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            tilPassword.setError("Invalid email or password");
            tilPassword.requestFocus();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}