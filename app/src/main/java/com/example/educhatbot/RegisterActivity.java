package com.example.educhatbot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText, emailEditText, passwordEditText;
    Button registerBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ✅ Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // ✅ Initialize Views
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);

        // ✅ Register Button Click
        registerBtn.setOnClickListener(v -> {

            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // ✅ Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Firebase Authentication (Create User)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            // 🔥 Get userId
                            String userId = mAuth.getCurrentUser().getUid();

                            // 🔥 Save username in Firebase Database
                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(userId);

                            ref.child("username").setValue(username);

                            Toast.makeText(RegisterActivity.this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT).show();

                            // ✅ Go to Login Screen
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {

                            Toast.makeText(RegisterActivity.this,
                                    "Registration Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}