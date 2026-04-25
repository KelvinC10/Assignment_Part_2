package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable full-screen mode
        setContentView(R.layout.activity_ai);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button Logic: Open the actual AI Chat interface
        Button btnOpenChatbot = findViewById(R.id.btnOpenChatbot);
        btnOpenChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(AI.this, ChatActivity.class);
            startActivity(intent);
        });

        // Button Logic: Return to the Home Activity
        Button btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        btnBackToDashboard.setOnClickListener(v -> {

            Intent intent = new Intent(AI.this, MainActivity.class);

            // Flags ensure we don't create multiple copies of the Home screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });


        // Navigation Bar setup and highlight the AI tab
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_AI);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Navigation bar for switching between Activities
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(getApplicationContext(), Location.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_food) {
                startActivity(new Intent(getApplicationContext(), Food.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_more) {
                startActivity(new Intent(getApplicationContext(), More.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}