package my.edu.utar.assignmentpart2;

public class LocationModel {
    private String name, description, imageUrl, city;
    private double latitude, longitude;

    // Empty constructor is REQUIRED for Firebase to work
    public LocationModel() {}

    public LocationModel(String name, String description, String imageUrl, String city,
                         double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}