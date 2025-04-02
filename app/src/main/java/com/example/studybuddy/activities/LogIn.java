package com.example.studybuddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;

public class LogIn extends AppCompatActivity {

    private Dialog dialog;
    private FirebaseAuth mAuth;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "token";
    private static final String NAME = "FullName";

    String name;
    RelativeLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // 🔥 Add this line FIRST
        setContentView(R.layout.activity_sign_up); // or activity_log_in
        mAuth = FirebaseAuth.getInstance(); // AFTER FirebaseApp is initialized
        // ...
    }

    public void f_pass(View view) {
        // Handle forgot password if needed
    }

    public void signIn_lS(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        String email_str = email.getText().toString().trim();
        String password_str = password.getText().toString().trim();

        String fieldsValidated = validateFields(email_str, password_str);

        if (fieldsValidated.equals("OK")) {
            name = getFirstName(email_str);
            loading();

            mAuth.signInWithEmailAndPassword(email_str, password_str)
                    .addOnCompleteListener(this, task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveData("firebase_dummy_token", name);
                            Intent intent = new Intent(LogIn.this, Dashboard.class);
                            LogIn.this.finish();
                            startActivity(intent);
                        } else {
                            show_err_snackBar("Login failed: " + task.getException().getMessage());
                        }
                    });
        } else {
            show_err_snackBar(fieldsValidated);
        }
    }

    private String getFirstName(String email_str) {
        String[] split = email_str.split("\\.");
        String name = (split[0].charAt(0) + "").toUpperCase() + split[0].substring(1);
        return name;
    }

    private void saveData(String token, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, token); // Replace with actual Firebase token if needed
        editor.putString(NAME, name);
        editor.apply();
    }

    public void sup_lS(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    public void makeToast(String message) {
        Toast.makeText(LogIn.this, message, Toast.LENGTH_LONG).show();
    }

    private void loading() {
        dialog.setContentView(R.layout.loading_message_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    void show_err_snackBar(String err_message) {
        loginLayout = findViewById(R.id.login_layout);

        Snackbar err_snackbar = Snackbar.make(loginLayout, "", Snackbar.LENGTH_INDEFINITE);
        View custom_snackbar_view = getLayoutInflater().inflate(R.layout.err_snackbar, null);
        err_snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) err_snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        TextView errText = custom_snackbar_view.findViewById(R.id.sb_error_text);
        errText.setText(err_message);
        custom_snackbar_view.findViewById(R.id.submit_sb).setOnClickListener(view -> err_snackbar.dismiss());
        snackbarLayout.addView(custom_snackbar_view, 0);
        err_snackbar.show();
    }

    private String validateFields(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return getString(R.string.empty_fields);
        }
        return "OK";
    }
}
