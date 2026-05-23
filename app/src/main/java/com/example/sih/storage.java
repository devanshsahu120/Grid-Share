package com.example.sih;

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

public class storage extends AppCompatActivity {

    TextView textView15, textView16, textView17, textView18;
    DatabaseReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_storage);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextViews
        textView15 = findViewById(R.id.textView15);
        textView16 = findViewById(R.id.textView16);
        textView17 = findViewById(R.id.textView17);
        textView18 = findViewById(R.id.textView18); // Status ("Normal" or "Overload")

        // Firebase Reference
        storageRef = FirebaseDatabase.getInstance().getReference("StorageData");

        // Fetch data once
        storageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(storage.this, "No storage data found in Firebase", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Read values safely
                double s1 = getSafeDouble(snapshot.child("storage1").getValue());
                double s2 = getSafeDouble(snapshot.child("storage2").getValue());
                double s3 = getSafeDouble(snapshot.child("storage3").getValue());
                String status = snapshot.child("status").getValue(String.class);

                // Display values
                textView15.setText(s1 + " kWh");
                textView16.setText(s2 + " kWh");
                textView17.setText(s3 + " kWh");
                textView18.setText(status != null ? status : "Normal");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(storage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Helper: safely convert number or string to double
    private double getSafeDouble(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Number) return ((Number) value).doubleValue();
            else return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
