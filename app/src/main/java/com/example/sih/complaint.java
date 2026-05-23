package com.example.sih;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class complaint extends AppCompatActivity {

    CheckBox prod, cons, stor, analy, live;
    EditText desc, name, contact;
    Button submit;

    DatabaseReference complaintsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        // Initialize Firebase reference
        complaintsRef = FirebaseDatabase.getInstance().getReference("complaints");

        // Initialize views
        prod = findViewById(R.id.check_production);
        cons = findViewById(R.id.check_consumption);
        stor = findViewById(R.id.check_storage);
        analy = findViewById(R.id.check_analytics);
        live = findViewById(R.id.check_live_data);
        desc = findViewById(R.id.edit_description);
        name = findViewById(R.id.edit_name);
        contact = findViewById(R.id.edit_contact);
        submit = findViewById(R.id.btn_submit_complaint);

        // Submit Button Click
        submit.setOnClickListener(v -> {
            StringBuilder issues = new StringBuilder();
            if (prod.isChecked()) issues.append("Production, ");
            if (cons.isChecked()) issues.append("Consumption, ");
            if (stor.isChecked()) issues.append("Storage, ");
            if (analy.isChecked()) issues.append("Analytics, ");
            if (live.isChecked()) issues.append("Live Data, ");

            String issueList = issues.toString().trim();
            String complaintText = desc.getText().toString().trim();
            String operator = name.getText().toString().trim();
            String phone = contact.getText().toString().trim();

            // ---------- Validation ----------
            if (TextUtils.isEmpty(operator)) {
                name.setError("Please enter your name");
                name.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                contact.setError("Enter valid contact number");
                contact.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(complaintText)) {
                desc.setError("Please describe your issue");
                desc.requestFocus();
                return;
            }
            if (issueList.isEmpty()) {
                Toast.makeText(this, "Please select at least one issue!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ---------- Prepare Data ----------
            String complaintId = complaintsRef.push().getKey(); // unique ID

            // Format date and time
            String dateTime = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
                    .format(new Date());

            HashMap<String, Object> complaintData = new HashMap<>();
            complaintData.put("id", complaintId);
            complaintData.put("operatorName", operator);
            complaintData.put("contactNumber", phone);
            complaintData.put("issues", issueList);
            complaintData.put("description", complaintText);
            complaintData.put("status", "Pending");
            complaintData.put("loggedAt", dateTime);

            // ---------- Push to Firebase ----------
            complaintsRef.child(Objects.requireNonNull(complaintId)).setValue(complaintData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
                        clearFields();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    private void clearFields() {
        prod.setChecked(false);
        cons.setChecked(false);
        stor.setChecked(false);
        analy.setChecked(false);
        live.setChecked(false);
        desc.setText("");
        name.setText("");
        contact.setText("");
    }
}
