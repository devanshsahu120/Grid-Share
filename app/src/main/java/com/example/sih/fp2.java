package com.example.sih;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class fp2 extends AppCompatActivity {

    Button b1, b2, b3, b4, b5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fp2);

        // Handle system UI padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Setup toolbar (must exist in activity_fp2.xml)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide title text
        }

        // ✅ Button navigation
        b1 = findViewById(R.id.button5);
        b2 = findViewById(R.id.button6);
        b3 = findViewById(R.id.button7);
        b4 = findViewById(R.id.button8);
        b5 = findViewById(R.id.button11);

        b1.setOnClickListener(v -> startActivity(new Intent(fp2.this, livedatapage.class)));
        b2.setOnClickListener(v -> startActivity(new Intent(fp2.this, production.class)));
        b3.setOnClickListener(v -> startActivity(new Intent(fp2.this, consumption.class)));
        b4.setOnClickListener(v -> startActivity(new Intent(fp2.this, storage.class)));
        b5.setOnClickListener(v -> startActivity(new Intent(fp2.this, efficiency.class)));
    }

    // ✅ Inflate the complaint icon menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // ✅ Handle menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_complain) {
            startActivity(new Intent(fp2.this, complaint.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
