package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class ChatActivity extends AppCompatActivity {

    private EditText etPrompt;
    private Button btnSend;
    private TextView tvReply;
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etPrompt = findViewById(R.id.etPrompt);
        btnSend = findViewById(R.id.btnSend);
        tvReply = findViewById(R.id.tvReply);

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");

        model = GenerativeModelFutures.from(ai);

        btnSend.setOnClickListener(v -> sendPrompt());
    }

    private void sendPrompt() {
        String userQuestion = etPrompt.getText().toString().trim();

        if (userQuestion.isEmpty()) {
            tvReply.setText("Please enter a question.");
            return;
        }

        btnSend.setEnabled(false);
        tvReply.setText("Thinking...");

        String placeData =
                "Known places in this app:\n" +
                        "- Lou Wong, Ipoh, famous for bean sprout chicken\n" +
                        "- Nam Heong White Coffee, Ipoh, traditional white coffee cafe\n" +
                        "- Plan B, Ipoh, modern cafe for brunch and coffee\n" +
                        "- Burps & Giggles, Ipoh, vintage cafe with desserts and coffee\n" +
                        "- Villa Seafood Restaurant, Lumut, seafood restaurant known for Thai-style seafood\n" +
                        "- Pangkor Island, Lumut, tropical island attraction\n" +
                        "- Taiping Lake Gardens, Taiping, scenic public park\n" +
                        "- Taiping Zoo & Night Safari, Taiping, zoo attraction\n" +
                        "- Kellie's Castle, Batu Gajah, historic mansion attraction\n" +
                        "- Leaning Tower, Teluk Intan, iconic landmark\n" +
                        "- Victoria Bridge, Kuala Kangsar, historic bridge\n" +
                        "- Lumut Waterfront, Lumut, seaside attraction\n" +
                        "- Lata Kinjang Waterfall, Tapah, waterfall attraction\n" +
                        "- Gunung Lang Recreational Park, Ipoh, nature park\n" +
                        "- Sitiawan Mangrove Park, Sitiawan, mangrove nature attraction\n";

        String promptText =
                "You are a helpful Perak tourism assistant. " +
                        "Answer briefly and clearly. " +
                        "Use the known places below when giving recommendations. " +
                        "Only answer about Perak tourism. " +
                        "If possible, recommend places from the list below.\n\n" +
                        placeData + "\n" +
                        "User question: " + userQuestion;

        Content prompt = new Content.Builder()
                .addText(promptText)
                .build();

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