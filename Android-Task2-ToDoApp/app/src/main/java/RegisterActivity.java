package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.utils.PasswordHasher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Registration screen: creates a new user account with hashed password storage.
 * On success, redirects to the login screen.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etEmail, etPassword, etConfirm;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        tilName = findViewById(R.id.til_reg_name);
        tilEmail = findViewById(R.id.til_reg_email);
        tilPassword = findViewById(R.id.til_reg_password);
        tilConfirm = findViewById(R.id.til_reg_confirm);
        etName = findViewById(R.id.et_reg_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirm = findViewById(R.id.et_reg_confirm);
        btnRegister = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_goto_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    /**
     * Validates all fields and attempts to create the account.
     */
    private void attemptRegister() {
        // Clear previous errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.err_empty_name));
            valid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.err_empty_email));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            valid = false;
        } else if (dbHelper.isEmailTaken(email)) {
            tilEmail.setError(getString(R.string.err_email_exists));
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.err_empty_password));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_short_password));
            valid = false;
        }

        if (confirm.isEmpty()) {
            tilConfirm.setError(getString(R.string.err_empty_password));
            valid = false;
        } else if (!confirm.equals(password)) {
            tilConfirm.setError(getString(R.string.err_password_mismatch));
            valid = false;
        }

        if (!valid) return;

        // Hash the password before storing
        String hashedPassword = PasswordHasher.hash(password);
        long userId = dbHelper.addUser(name, email, hashedPassword);

        if (userId != -1) {
            Toast.makeText(this, R.string.toast_register_success, Toast.LENGTH_SHORT).show();
            // Go back to login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {
            Toast.makeText(this, R.string.err_register_failed, Toast.LENGTH_SHORT).show();
        }
    }
}