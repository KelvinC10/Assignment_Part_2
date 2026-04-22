package my.edu.utar.assignmentpart2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public List<Weather> weather;

    public class Main {
        @SerializedName("temp")
        public float temp;
    }

    public class Weather {
        @SerializedName("description")
        public String description;
    }
}
