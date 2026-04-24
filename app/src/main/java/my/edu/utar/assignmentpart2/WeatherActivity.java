package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private TextView weatherTextView;
    private Button getWeatherButton;

    private static final String API_KEY = "PUT_YOUR_OPENWEATHER_API_KEY_HERE";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherTextView = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(v -> fetchWeatherData());
    }

    private void fetchWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call =
                service.getCurrentWeather("Kampar,MY", API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    if (weatherResponse.main != null &&
                            weatherResponse.weather != null &&
                            !weatherResponse.weather.isEmpty()) {

                        float temperature = weatherResponse.main.temp;
                        String description = weatherResponse.weather.get(0).description;

                        weatherTextView.setText("Kampar: " + temperature + "°C\n" + description);
                    } else {
                        weatherTextView.setText("Weather data is incomplete.");
                    }

                } else {
                    try {
                        String error = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Unknown error";

                        Log.e("WEATHER_ERROR", error);
                        weatherTextView.setText("Failed to get weather.\nCheck API key or city name.");

                    } catch (IOException e) {
                        weatherTextView.setText("Failed to read error response.");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WEATHER_FAILURE", "Network error", t);
                Toast.makeText(WeatherActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}