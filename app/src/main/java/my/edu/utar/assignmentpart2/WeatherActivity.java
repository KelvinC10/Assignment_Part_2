package my.edu.utar.assignmentpart2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout weatherContainer;
    private TextView tvLastUpdated;
    private TextView weatherTextView;
    private Button getWeatherButton;

    private static final String BASE_URL = "https://api.open-meteo.com/v1/";

    private static final double[][] CITIES = {
            {4.5975, 101.0901},  // Ipoh
            {4.3241, 101.1357},  // Kampar
            {4.8500, 100.7333},  // Taiping
            {4.0267, 101.0228},  // Teluk Intan
            {4.7667, 100.9333},  // Kuala Kangsar
            {4.2167, 100.7000},  // Sitiawan
            {4.4667, 101.0333},  // Batu Gajah
            {4.2333, 100.6167},  // Lumut
            {5.0167, 101.0333},  // Lenggong
            {4.6833, 101.1500},  // Sungai Siput
    };

    private static final String[] CITY_NAMES = {
            "Ipoh", "Kampar", "Taiping", "Teluk Intan",
            "Kuala Kangsar", "Sitiawan", "Batu Gajah",
            "Lumut", "Lenggong", "Sungai Siput"
    };

    private static class WeatherData {
        float temperature;
        int weathercode;
        float windspeed;
    }

    private final Map<String, WeatherData> resultsMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherContainer = findViewById(R.id.weatherContainer);
        tvLastUpdated    = findViewById(R.id.tvLastUpdated);
        weatherTextView  = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(v -> fetchAllCities());
    }

    private void fetchAllCities() {
        getWeatherButton.setEnabled(false);
        getWeatherButton.setText("Loading...");
        weatherTextView.setVisibility(View.VISIBLE);
        weatherTextView.setText("Fetching weather for all Perak cities...");
        resultsMap.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        AtomicInteger completed = new AtomicInteger(0);

        for (int i = 0; i < CITIES.length; i++) {
            final String cityName = CITY_NAMES[i];
            Call<WeatherResponse> call = service.getCurrentWeather(CITIES[i][0], CITIES[i][1], true);

            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse.CurrentWeather cw = response.body().current_weather;
                        if (cw != null) {
                            WeatherData data  = new WeatherData();
                            data.temperature  = cw.temperature;
                            data.weathercode  = cw.weathercode;
                            data.windspeed    = cw.windspeed;
                            resultsMap.put(cityName, data);
                        }
                    }
                    if (completed.incrementAndGet() == CITIES.length) {
                        displayResults();
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    if (completed.incrementAndGet() == CITIES.length) {
                        displayResults();
                    }
                    Toast.makeText(WeatherActivity.this, "Error loading " + cityName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void displayResults() {
        runOnUiThread(() -> {
            weatherTextView.setVisibility(View.GONE);

            // Remove old cards
            if (weatherContainer.getChildCount() > 1) {
                weatherContainer.removeViews(1, weatherContainer.getChildCount() - 1);
            }

            LayoutInflater inflater = LayoutInflater.from(this);

            for (String cityName : CITY_NAMES) {
                WeatherData data = resultsMap.get(cityName);
                View card = inflater.inflate(R.layout.item_weather_card, weatherContainer, false);

                TextView tvIcon = card.findViewById(R.id.tvWeatherIcon);
                TextView tvCity = card.findViewById(R.id.tvCityName);
                TextView tvCond = card.findViewById(R.id.tvCondition);
                TextView tvWind = card.findViewById(R.id.tvWind);
                TextView tvTemp = card.findViewById(R.id.tvTemperature);

                if (data != null) {
                    tvIcon.setText(getWeatherIcon(data.weathercode));
                    tvCity.setText("📍 " + cityName);
                    tvCond.setText(getWeatherCondition(data.weathercode));
                    tvWind.setText("💨 Wind: " + data.windspeed + " km/h");
                    tvTemp.setText((int) data.temperature + "°");
                } else {
                    tvIcon.setText("❌");
                    tvCity.setText("📍 " + cityName);
                    tvCond.setText("Failed to load");
                    tvWind.setText("");
                    tvTemp.setText("--°");
                }

                weatherContainer.addView(card);
            }

            String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
            tvLastUpdated.setText("Last updated: " + time);
            getWeatherButton.setEnabled(true);
            getWeatherButton.setText("🔄  Refresh Perak Weather");
        });
    }

    private String getWeatherIcon(int code) {
        if (code == 0)        return "☀️";
        else if (code <= 2)   return "⛅";
        else if (code == 3)   return "☁️";
        else if (code <= 49)  return "🌫️";
        else if (code <= 59)  return "🌦️";
        else if (code <= 69)  return "🌧️";
        else if (code <= 79)  return "🌨️";
        else if (code <= 82)  return "🌧️";
        else if (code <= 86)  return "🌨️";
        else if (code <= 99)  return "⛈️";
        else                  return "🌡️";
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