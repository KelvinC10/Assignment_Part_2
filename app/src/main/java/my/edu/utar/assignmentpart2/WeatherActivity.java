package my.edu.utar.assignmentpart2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private TextView weatherTextView;
    private Button getWeatherButton;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String BASE_URL = "https://api.open-meteo.com/v1/";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherTextView = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getWeatherButton.setOnClickListener(v -> checkLocationPermission());
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserLocation() {
        weatherTextView.setText("Detecting your location...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                getCityFromLocation(location);
            } else {
                weatherTextView.setText("Could not detect location.\nPlease enable GPS and try again.");
            }
        });
    }

    private void getCityFromLocation(Location location) {
        double latitude  = location.getLatitude();
        double longitude = location.getLongitude();

        // Convert GPS coordinates to city name
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String cityName = "Your Location";
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Try to get city, fallback to subAdmin or state
                if (address.getLocality() != null) {
                    cityName = address.getLocality();
                } else if (address.getSubAdminArea() != null) {
                    cityName = address.getSubAdminArea();
                } else if (address.getAdminArea() != null) {
                    cityName = address.getAdminArea();
                }
            }
            fetchWeather(latitude, longitude, cityName);
        } catch (IOException e) {
            fetchWeather(latitude, longitude, "Your Location");
        }
    }

    private void fetchWeather(double latitude, double longitude, String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeather(latitude, longitude, true);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse.CurrentWeather cw = response.body().current_weather;
                    if (cw != null) {
                        weatherTextView.setText(
                                "📍 " + cityName + "\n\n" +
                                        "🌡 Temperature : " + cw.temperature + " °C\n" +
                                        "🌤 Condition   : " + getWeatherCondition(cw.weathercode) + "\n" +
                                        "💨 Wind Speed  : " + cw.windspeed + " km/h"
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
                Toast.makeText(WeatherActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getWeatherCondition(int code) {
        if (code == 0)        return "Clear Sky ☀️";
        else if (code <= 2)   return "Partly Cloudy ⛅";
        else if (code == 3)   return "Overcast ☁️";
        else if (code <= 49)  return "Foggy 🌫️";
        else if (code <= 59)  return "Drizzle 🌦️";
        else if (code <= 69)  return "Rainy 🌧️";
        else if (code <= 79)  return "Snowy 🌨️";
        else if (code <= 82)  return "Rain Showers 🌧️";
        else if (code <= 86)  return "Snow Showers 🌨️";
        else if (code <= 99)  return "Thunderstorm ⛈️";
        else                  return "Unknown";
    }
}