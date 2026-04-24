package my.edu.utar.assignmentpart2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public List<Weather> weather;

    public static class Main {
        @SerializedName("temp")
        public float temp;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;
    }
}