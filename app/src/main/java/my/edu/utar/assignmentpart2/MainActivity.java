package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Find the TextView by its ID from your XML
        TextView tvDate = findViewById(R.id.tvDate);

        // 2. Get the current date from the system
        Calendar calendar = Calendar.getInstance();

        // 3. Define the format (Example: 14/4/2026)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // 4. Use getString to safely combine the text
        String dateToShow = getString(R.string.date_format, currentDate);
        tvDate.setText(dateToShow);


    }


}
