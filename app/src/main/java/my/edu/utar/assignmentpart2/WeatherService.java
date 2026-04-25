package my.edu.utar.assignmentpart2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    // Interface for the Retrofit library to handle network requests.
    // It defines the API endpoints and the parameters needed to get weather data.
    @GET("forecast")
    Call<WeatherResponse> getCurrentWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current_weather") boolean currentWeather
    );
}