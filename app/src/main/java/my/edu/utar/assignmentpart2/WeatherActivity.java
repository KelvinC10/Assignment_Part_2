package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private TextView weatherTextView;
    private Button getWeatherButton;

    private static final String BASE_URL = "https://api.open-meteo.com/v1/";
    private static final double LATITUDE  = 4.3241;
    private static final double LONGITUDE = 101.1357;

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
        Call<WeatherResponse> call = service.getCurrentWeather(LATITUDE, LONGITUDE, true);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse.CurrentWeather cw = response.body().current_weather;
                    if (cw != null) {
                        weatherTextView.setText(
                                "Kampar, Malaysia\n" +
                                        "Temperature: " + cw.temperature + " °C\n" +
                                        "Condition: " + getWeatherCondition(cw.weathercode) + "\n" +
                                        "Wind Speed: " + cw.windspeed + " km/h"
                        );
                    } else {
                        weatherTextView.setText("Weather data is incomplete.");
                    }
                } else {
                    weatherTextView.setText("Failed to get weather.\nPlease try again.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getWeatherCondition(int code) {
        if (code == 0)        return "Clear Sky";
        else if (code <= 2)   return "Partly Cloudy";
        else if (code == 3)   return "Overcast";
        else if (code <= 49)  return "Foggy";
        else if (code <= 59)  return "Drizzle";
        else if (code <= 69)  return "Rainy";
        else if (code <= 79)  return "Snowy";
        else if (code <= 82)  return "Rain Showers";
        else if (code <= 86)  return "Snow Showers";
        else if (code <= 99)  return "Thunderstorm";
        else                  return "Unknown";
    }
}