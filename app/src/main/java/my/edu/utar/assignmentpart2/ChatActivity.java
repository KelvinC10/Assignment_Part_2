package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedHashSet;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private EditText etPrompt;
    private Button btnSend;
    private TextView tvReply;
    private ImageButton btnBackArrow; // New variable
    private GenerativeModelFutures model;
    private FirebaseFirestore db;

    // A list to store unique descriptions of places to help the AI learn
    private final Set<String> placeLines = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize UI Elements
        etPrompt = findViewById(R.id.etPrompt);
        btnSend = findViewById(R.id.btnSend);
        tvReply = findViewById(R.id.tvReply);
        btnBackArrow = findViewById(R.id.btnBackArrow); // Initialize the arrow

        // Close the chat and return to the previous screen when the arrow is clicked
        btnBackArrow.setOnClickListener(v -> {
            finish();
        });

        // Setup Firebase and the AI Model (Gemini)
        db = FirebaseFirestore.getInstance();
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");
        model = GenerativeModelFutures.from(ai);

        // Prepare the AI by loading all tourism data into memory
        loadAllPlaceData();

        btnSend.setOnClickListener(v -> sendPrompt());
    }

    // Triggers data downloads from all tourism and food collections.
    private void loadAllPlaceData() {
        loadCollection("Best Attraction Places");
        loadCollection("Local Recommendation Places");
        loadCollection("Food");
        loadCollection("Location Best Attraction Places");
        loadCollection("Location Local Recommendation Places");
        loadCollection("Food Page Food");
    }

    // Downloads specific collection data and formats it for the AI to understand.
    private void loadCollection(String collectionName) {
        db.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String city = document.getString("city");
                        String description = document.getString("description");

                        if (name != null && city != null && description != null) {
                            // Trim description so the AI prompt doesn't get too long
                            String shortDescription = description.trim();
                            if (shortDescription.length() > 100) {
                                shortDescription = shortDescription.substring(0, 100) + "...";
                            }
                            // Store the formatted info
                            placeLines.add("- " + name + ", " + city + ", " + shortDescription);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Silently fail or log error
                });
    }

    // Sends the user's question and the app's data to the AI for a response
    private void sendPrompt() {
        String userQuestion = etPrompt.getText().toString().trim();

        if (userQuestion.isEmpty()) {
            tvReply.setText("Please enter a question.");
            return;
        }

        // Disable button while waiting for the AI to "think"
        btnSend.setEnabled(false);
        tvReply.setText("Thinking...");

        // Combine all loaded place data into one big text block for the AI
        StringBuilder placeDataBuilder = new StringBuilder();
        placeDataBuilder.append("Known places in this app:\n");

        if (placeLines.isEmpty()) {
            placeDataBuilder.append("- No place data loaded yet.\n");
        } else {
            for (String line : placeLines) {
                placeDataBuilder.append(line).append("\n");
            }
        }

        String placeData = placeDataBuilder.toString();

        // Create the instructions (System Prompt) for the AI
        String promptText =
                "You are a helpful Perak tourism assistant. " +
                        "Answer clearly and briefly. " +
                        "Prefer recommending places and foods from the known app data below. " +
                        "If relevant, you may also mention other well-known places or foods in Perak. " +
                        "Try to match the user's request by city, food type, or attraction type. " +
                        "If the user asks something outside Perak, say you only support Perak tourism.\n\n" +
                        placeData + "\n" +
                        "User question: " + userQuestion;

        // Build the final message for the AI model
        Content prompt = new Content.Builder()
                .addText(promptText)
                .build();

        // Send the message and wait for the reply
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                btnSend.setEnabled(true);
                String text = result.getText();
                tvReply.setText(text != null ? text : "No response.");
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                btnSend.setEnabled(true);
                tvReply.setText("Error: " + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }
}