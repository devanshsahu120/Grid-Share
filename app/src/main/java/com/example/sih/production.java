package com.example.sih;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class production extends AppCompatActivity {

    TextView textView, textView7, textView8, textView9, textView10;
    DatabaseReference prodRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_production);

        // Handle window insets (UI safe area)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextViews
        textView = findViewById(R.id.textView);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);

        // Firebase reference (e.g., "ProductionData")
        prodRef = FirebaseDatabase.getInstance().getReference("ProductionData");

        // Get data from Firebase
        prodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        double val1 = snapshot.child("solar1").getValue(Double.class);
                        double val2 = snapshot.child("solar2").getValue(Double.class);
                        double val3 = snapshot.child("solar3").getValue(Double.class);
                        double val4 = snapshot.child("solar4").getValue(Double.class);

                        // Set text with units
                        textView.setText(val1 + " kWh");
                        textView7.setText(val2 + " kWh");
                        textView8.setText(val3 + " kWh");
                        textView9.setText(val4 + " kWh");

                        // Calculate total
                        double total = val1 + val2 + val3 + val4;
                        total = Math.round(total * 100.0) / 100.0;
                        textView10.setText(total + " kWh");
                    } catch (Exception e) {
                        Toast.makeText(production.this, "Error reading values: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(production.this, "No data found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(production.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
