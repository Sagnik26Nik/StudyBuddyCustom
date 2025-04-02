package com.example.studybuddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Dialog dialog;
    private RelativeLayout signup_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // 🔥 Add this line FIRST
        setContentView(R.layout.activity_sign_up); // or activity_log_in
        mAuth = FirebaseAuth.getInstance(); // AFTER FirebaseApp is initialized
        // ...
    }

    public void signUp_supS(View view) {
        HashMap<String, String> data = getData();

        if (!data.isEmpty()) {
            String email_str = data.get("email");
            String password_str = data.get("password");

            loading(); // show loading dialog

            mAuth.createUserWithEmailAndPassword(email_str, password_str)
                    .addOnCompleteListener(this, task -> {
                        dialog.dismiss(); // close loading popup

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            open_success_dialog("Account created successfully for: " + user.getEmail());
                        } else {
                            show_err_snackBar("Signup failed: " + task.getException().getMessage());
                        }
                    });
        }
    }

    public void sin_sup(View view) {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
    }

    public boolean emailValidation(String Email) {
        char[] ec_arr = Email.toCharArray();
        int occ = 0;
        for (char x : ec_arr) {
            if (x == '@') occ++;
        }

        if (occ == 1) {
            String[] e_arr = Email.split("@");
            if (e_arr[1].equalsIgnoreCase(getString(R.string.vit_domain))) {
                int year = Integer.parseInt(e_arr[0].substring(e_arr[0].length() - 4));
                int currentYear = 1900 + new Date().getYear(); // deprecated but works here
                return year < currentYear && year > 1969;
            }
        }
        return false;
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        EditText email = findViewById(R.id.email_sup);
        EditText name = findViewById(R.id.name_sup);
        EditText password = findViewById(R.id.pass_sup);
        EditText registration_number = findViewById(R.id.regno);
        EditText major = findViewById(R.id.major);

        String email_str = email.getText().toString().trim();
        String name_str = name.getText().toString().trim();
        String password_str = password.getText().toString().trim();
        String registration_number_str = registration_number.getText().toString().trim();
        String major_str = major.getText().toString().trim();

        if (email_str.isEmpty() || name_str.isEmpty() || password_str.isEmpty()
                || registration_number_str.isEmpty() || major_str.isEmpty()) {
            show_err_snackBar(getString(R.string.empty_fields));
        } else {
            String invalidFields = matchRegex(email_str, registration_number_str).trim();
            if (!invalidFields.isEmpty()) {
                String error_text = invalidFields.replace(" ", ",").replace("-", " ");
                String[] fieldArray = invalidFields.split(" ");
                String verb = (fieldArray.length > 1) ? "are" : "is";
                show_err_snackBar(error_text + " " + verb + " invalid");
            } else {
                data.put("email", email_str);
                data.put("name", name_str);
                data.put("password", password_str);
                data.put("registration_number", registration_number_str);
                data.put("major", major_str);
            }
        }
        return data;
    }

    private String matchRegex(String email_str, String registration_number_str) {
        String fields = "";
        if (!emailValidation(email_str)) fields += "Email ";
        if (!registration_number_str.matches(getString(R.string.reg_no_regex)))
            fields += "Registration-Number ";
        return fields;
    }

    private void loading() {
        dialog.setContentView(R.layout.loading_message_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void open_success_dialog(String message) {
        dialog.setContentView(R.layout.success_popup_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.message);
        textView.setText(message);
        Button button = dialog.findViewById(R.id.submit_sb);
        button.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(SignUp.this, WelcomeScreen.class);
            SignUp.this.finish();
            startActivity(intent);
        });
        dialog.show();
    }

    void show_err_snackBar(String err_message) {
        signup_layout = findViewById(R.id.sup_layout);
        Snackbar err_snackbar = Snackbar.make(signup_layout, "", Snackbar.LENGTH_INDEFINITE);
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
}
