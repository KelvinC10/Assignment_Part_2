package my.edu.utar.assignmentpart2;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Activity class that handles the Google Maps integration.
// It displays a specific tourist spot or food location on a map using coordinates.
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Find the map fragment from the XML layout and prepare it for use
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            // Start the map in the background so the app doesn't freeze while loading
            mapFragment.getMapAsync(this);
        }
    }

    // This runs once the Google Map is fully loaded and ready to use.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Retrieve the location details passed from the previous screen (Location or Food)
        String placeName = getIntent().getStringExtra("placeName");
        String city = getIntent().getStringExtra("city");
        // Get coordinates
        double lat = getIntent().getDoubleExtra("lat", 4.5975);
        double lng = getIntent().getDoubleExtra("lng", 101.0901);

        // Combine Latitude and Longitude into a single 'LatLng' object
        LatLng place = new LatLng(lat, lng);

        // Add a red marker on the map at the specific location
        mMap.addMarker(new MarkerOptions()
                .position(place)
                .title(placeName)
                .snippet(city));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f));
    }
}