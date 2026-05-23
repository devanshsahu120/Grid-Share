package com.example.sih;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button btnSubmit;
    DatabaseReference usersRef;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnSubmit = findViewById(R.id.btnSubmit);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking credentials...");
        progressDialog.setCancelable(false);

        btnSubmit.setOnClickListener(v -> {
            String enteredEmail = username.getText().toString().trim();
            String enteredPassword = password.getText().toString().trim();

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                username.setError("Enter a valid email address");
                username.requestFocus();
                return;
            }

            if (enteredPassword.length() < 3) {
                password.setError("Password must be at least 3 characters");
                password.requestFocus();
                return;
            }

            progressDialog.show();

            // ✅ --- Temporary Login (Hardcoded) ---
            if (enteredEmail.equalsIgnoreCase("admin@gmail.com") && enteredPassword.equals("1234")) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, fp2.class));
                finish();
                return;
            }

            // --- Firebase Check ---
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean found = false;

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbEmail = String.valueOf(userSnapshot.child("email").getValue());
                        String dbPassword = String.valueOf(userSnapshot.child("password").getValue());

                        if (dbEmail.equalsIgnoreCase(enteredEmail) && dbPassword.equals(enteredPassword)) {
                            found = true;
                            break;
                        }
                    }

                    progressDialog.dismiss();

                    if (found) {
                        Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, fp2.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_help) {
            Intent i = new Intent(Login.this, faq.class);
            startActivity(i);
            return true;
        } else if (id == R.id.menu_contact) {
            showContactDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showContactDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Contact Us")
                .setMessage("📧 Email: support@communitygrid.com\n📞 Phone: +91 76490 11224")
                .setPositiveButton("OK", null)
                .show();
    }
}