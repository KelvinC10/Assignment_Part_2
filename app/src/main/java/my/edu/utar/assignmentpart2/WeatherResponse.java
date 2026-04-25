package my.edu.utar.assignmentpart2;

import com.google.gson.annotations.SerializedName;

// This is a Data Model class used for the Weather API.
// It helps the 'Retrofit' library convert JSON data from the internet into Java variables so that our app can understand.
public class WeatherResponse {

    // @SerializedName links the exact name in the API data to our Java variable
    @SerializedName("current_weather")
    public CurrentWeather current_weather;

    // This nested class represents the specific weather details inside the "current_weather" section of the API response.
    public static class CurrentWeather {
        @SerializedName("temperature")
        public float temperature; // Stores the current heat/cold level

        @SerializedName("weathercode")
        public int weathercode; // Stores a number representing the sky condition (e.g., 0 for sunny)

        @SerializedName("windspeed")
        public float windspeed; // Stores how fast the wind is blowing
    }
}