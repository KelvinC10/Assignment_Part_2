package my.edu.utar.assignmentpart2;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("current_weather")
    public CurrentWeather current_weather;

    public static class CurrentWeather {
        @SerializedName("temperature")
        public float temperature;

        @SerializedName("weathercode")
        public int weathercode;

        @SerializedName("windspeed")
        public float windspeed;
    }
}