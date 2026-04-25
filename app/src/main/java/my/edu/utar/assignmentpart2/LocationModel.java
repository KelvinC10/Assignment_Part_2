package my.edu.utar.assignmentpart2;

public class LocationModel {
    private String name, description, imageUrl, city;
    private double latitude, longitude;

    // Empty constructor is REQUIRED for Firebase to work
    public LocationModel() {}

    // A constructor to manually create a Location object.
    public LocationModel(String name, String description, String imageUrl, String city,
                         double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter methods used by Adapters and DetailsActivity to display data
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCity() { return city; }

    // Coordinates used for Google Maps API integration
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}